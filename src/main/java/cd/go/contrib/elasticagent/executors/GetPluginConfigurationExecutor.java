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

package cd.go.contrib.elasticagent.executors;

import cd.go.contrib.elasticagent.RequestExecutor;
import cd.go.contrib.elasticagent.model.*;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import static cd.go.contrib.elasticagent.utils.Util.GSON;

public class GetPluginConfigurationExecutor implements RequestExecutor {
    public static final Map<String, Field> FIELDS = new LinkedHashMap<>();
    public static final Field GO_SERVER_URL = new GoServerUrlField("go_server_url", "GoCD server URL", false, "0");
    public static final Field AUTO_REGISTER_TIMEOUT = new PositiveNumberField("auto_register_timeout", "Agent auto-register timeout (in minutes)", "10", false, false, "1");
    public static final Field MAX_PENDING_PODS = new PositiveNumberField("pending_pods_count", "Maximum pending pods", "10", false, false, "2");
    public static final Field CLUSTER_URL = new HttpsURLField("kubernetes_cluster_url", "Cluster URL", true, "3");
    public static final Field NAMESPACE = new Field("namespace", "Namespace", "default", false, false, "5");
    public static final Field SECURITY_TOKEN = new NonBlankField("security_token", "Security token", true, "6");
    public static final Field CLUSTER_CA_CERT = new Field("kubernetes_cluster_ca_cert", "Cluster ca certificate", null, false, true, "7");
    public static final Field USE_PROFILE_PENDING_PODS_COUNT = new Field("use_profile_pending_pods_count", "Use Profile pending pods count", "true", true, false, "4");

    static {
        FIELDS.put(GO_SERVER_URL.key(), GO_SERVER_URL);
        FIELDS.put(AUTO_REGISTER_TIMEOUT.key(), AUTO_REGISTER_TIMEOUT);
        FIELDS.put(MAX_PENDING_PODS.key(), MAX_PENDING_PODS);
        FIELDS.put(CLUSTER_URL.key(), CLUSTER_URL);
        FIELDS.put(CLUSTER_CA_CERT.key(), CLUSTER_CA_CERT);
        FIELDS.put(SECURITY_TOKEN.key(), SECURITY_TOKEN);
        FIELDS.put(NAMESPACE.key(), NAMESPACE);
        FIELDS.put(USE_PROFILE_PENDING_PODS_COUNT.key(), NAMESPACE);
    }

    public GoPluginApiResponse execute() {
        return DefaultGoPluginApiResponse.success(GSON.toJson(FIELDS));
    }
}
