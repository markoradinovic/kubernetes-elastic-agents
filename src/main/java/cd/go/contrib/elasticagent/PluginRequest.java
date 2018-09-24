/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagent;

import cd.go.contrib.elasticagent.model.ServerInfo;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

import static cd.go.contrib.elasticagent.Constants.*;
import static cd.go.contrib.elasticagent.KubernetesPlugin.LOG;
import static cd.go.contrib.elasticagent.utils.Util.catFile;
import static java.text.MessageFormat.format;

public class PluginRequest {
    private final GoApplicationAccessor accessor;

    public PluginRequest(GoApplicationAccessor accessor) {
        this.accessor = accessor;
    }

    public ServerInfo getSeverInfo() throws ServerRequestFailedException {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_INFO, SERVER_INFO_API_VERSION, PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.serverInfo(response);
        }

        return ServerInfo.fromJSON(response.responseBody());
    }

    public PluginSettings getPluginSettings() throws ServerRequestFailedException {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_GET_PLUGIN_SETTINGS, PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.getPluginSettings(response);
        }

        PluginSettings pluginSettings = PluginSettings.fromJSON(response.responseBody());
        if (pluginSettings == null) {
            return createUsingPodEnvironment();
        }
        return pluginSettings;
    }

    private PluginSettings createUsingPodEnvironment() {
        LOG.info("Trying to create PluginSettings using Pod Environment.");

        String goServerUrl = "https://" + System.getenv("GOCD_PORT_8154_TCP_ADDR") + ":8154/go";
        if (StringUtils.isBlank(goServerUrl)) {
            LOG.error("Environment variable [GOCD_PORT_8154_TCP_ADDR] is missing or not set.");
            throw new PluginSettingsNotConfiguredException();
        }
        LOG.debug("Found: GO_SERVER_URL=" + goServerUrl);


        String k8sServiceHost = System.getenv("KUBERNETES_SERVICE_HOST");
        String k8sServicePort = System.getenv("KUBERNETES_SERVICE_PORT");
        if (StringUtils.isBlank(k8sServiceHost) || StringUtils.isBlank(k8sServicePort)) {
            LOG.error("Environment variables [KUBERNETES_SERVICE_HOST] and [KUBERNETES_SERVICE_PORT] are missing or not set.");
            throw new PluginSettingsNotConfiguredException();
        }
        String k8sUrl = "https://" + k8sServiceHost + ":" + k8sServicePort;
        LOG.debug("Found: K8S_URL=" + k8sUrl);

        String namespace = catFile("/var/run/secrets/kubernetes.io/serviceaccount/namespace");
        if (StringUtils.isBlank(namespace)) {
            LOG.error("Cannot read file [/var/run/secrets/kubernetes.io/serviceaccount/namespace]. Probably missing RBAC ServiceAccount.");
            throw new PluginSettingsNotConfiguredException();
        }
        LOG.debug("Found: K8S_NAMESPACE=" + namespace);

        String token = catFile("/var/run/secrets/kubernetes.io/serviceaccount/token");
        if (StringUtils.isBlank(token)) {
            LOG.error("Cannot read file [/var/run/secrets/kubernetes.io/serviceaccount/token].  Probably missing RBAC ServiceAccount.");
            throw new PluginSettingsNotConfiguredException();
        }
        LOG.debug("Found: K8S_TOKEN=" + token);

        String caCrt = catFile("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt");
        if (StringUtils.isBlank(caCrt)) {
            LOG.error("Cannot read file [/var/run/secrets/kubernetes.io/serviceaccount/ca.crt].  Probably missing RBAC ServiceAccount.");
            throw new PluginSettingsNotConfiguredException();
        }
        LOG.debug("Found: K8S_TCA_CRT=" + caCrt);

        return new PluginSettings(goServerUrl, k8sUrl, token, caCrt, namespace);
    }

    public Agents listAgents() throws ServerRequestFailedException {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_LIST_AGENTS, PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.listAgents(response);
        }

        return new Agents(Agent.fromJSONArray(response.responseBody()));
    }

    public void disableAgents(Collection<Agent> toBeDisabled) throws ServerRequestFailedException {
        LOG.debug(format("[Server Ping] Disabling Agents: {0}", toBeDisabled.toString()));
        if (toBeDisabled.isEmpty()) {
            return;
        }

        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_DISABLE_AGENT, PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        request.setRequestBody(Agent.toJSONArray(toBeDisabled));

        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.disableAgents(response);
        }
    }

    public void deleteAgents(Collection<Agent> toBeDeleted) throws ServerRequestFailedException {
        LOG.debug(format("[Server Ping] Deleting Agents: {0}", toBeDeleted.toString()));
        if (toBeDeleted.isEmpty()) {
            return;
        }

        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_DELETE_AGENT, PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        request.setRequestBody(Agent.toJSONArray(toBeDeleted));
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.deleteAgents(response);
        }
    }
}
