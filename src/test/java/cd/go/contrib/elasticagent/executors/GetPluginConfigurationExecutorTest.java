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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GetPluginConfigurationExecutorTest {

    @Test
    public void shouldSerializeAllFields() {
        GoPluginApiResponse response = new GetPluginConfigurationExecutor().execute();
        Map<String, Object> hashMap = new Gson().fromJson(response.responseBody(),
                new TypeToken<Map<String, Object>>() {
                }.getType());
        assertEquals("Are you using anonymous inner classes — see https://github.com/google/gson/issues/298",
                hashMap.size(), GetPluginConfigurationExecutor.FIELDS.size());
    }

    @Test
    public void assertJsonStructure() throws Exception {
        GoPluginApiResponse response = new GetPluginConfigurationExecutor().execute();

        assertThat(response.responseCode(), is(200));
        String expectedJSON = "{\"go_server_url\":{\"display-name\":\"GoCD server URL\",\"required\":false,\"secure\":false,\"display-order\":\"0\"},\"auto_register_timeout\":{\"display-name\":\"Agent auto-register timeout (in minutes)\",\"default-value\":\"10\",\"required\":false,\"secure\":false,\"display-order\":\"1\"},\"pending_pods_count\":{\"display-name\":\"Maximum pending pods\",\"default-value\":\"10\",\"required\":false,\"secure\":false,\"display-order\":\"2\"},\"kubernetes_cluster_url\":{\"display-name\":\"Cluster URL\",\"required\":true,\"secure\":false,\"display-order\":\"3\"},\"kubernetes_cluster_ca_cert\":{\"display-name\":\"Cluster ca certificate\",\"required\":false,\"secure\":true,\"display-order\":\"7\"},\"security_token\":{\"display-name\":\"Security token\",\"required\":true,\"secure\":true,\"display-order\":\"6\"},\"namespace\":{\"display-name\":\"Namespace\",\"default-value\":\"default\",\"required\":false,\"secure\":false,\"display-order\":\"5\"},\"use_profile_pending_pods_count\":{\"display-name\":\"Namespace\",\"default-value\":\"default\",\"required\":false,\"secure\":false,\"display-order\":\"5\"}}";
        //        String expectedJSON = "{\n" +
        //                "  \"go_server_url\": {\n" +
        //                "    \"display-name\": \"GoCD server URL\",\n" +
        //                "    \"required\": false,\n" +
        //                "    \"secure\": false,\n" +
        //                "    \"display-order\": \"0\"\n" +
        //                "  },\n" +
        //                "  \"auto_register_timeout\": {\n" +
        //                "    \"display-name\": \"Agent auto-register timeout (in minutes)\",\n" +
        //                "    \"default-value\": \"10\",\n" +
        //                "    \"required\": false,\n" +
        //                "    \"secure\": false,\n" +
        //                "    \"display-order\": \"1\"\n" +
        //                "  },\n" +
        //                "  \"pending_pods_count\": {\n" +
        //                "    \"display-name\": \"Maximum pending pods\",\n" +
        //                "    \"default-value\": \"10\",\n" +
        //                "    \"required\": false,\n" +
        //                "    \"secure\": false,\n" +
        //                "    \"display-order\": \"2\"\n" +
        //                "  },\n" +
        //                "  \"kubernetes_cluster_url\": {\n" +
        //                "    \"display-name\": \"Cluster URL\",\n" +
        //                "    \"required\": true,\n" +
        //                "    \"secure\": false,\n" +
        //                "    \"display-order\": \"3\"\n" +
        //                "  },\n" +
        //                "  \"namespace\": {\n" +
        //                "    \"display-name\": \"Namespace\",\n" +
        //                "    \"default-value\": \"default\",\n" +
        //                "    \"required\": false,\n" +
        //                "    \"secure\": false,\n" +
        //                "    \"display-order\": \"4\"\n" +
        //                "  },\n" +
        //                "  \"security_token\": {\n" +
        //                "    \"display-name\": \"Security token\",\n" +
        //                "    \"required\": true,\n" +
        //                "    \"secure\": true,\n" +
        //                "    \"display-order\": \"5\"\n" +
        //                "  },\n" +
        //                "  \"kubernetes_cluster_ca_cert\": {\n" +
        //                "    \"display-name\": \"Cluster ca certificate\",\n" +
        //                "    \"required\": false,\n" +
        //                "    \"secure\": true,\n" +
        //                "    \"display-order\": \"6\"\n" +
        //                "  }\n" +
        //                "}";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}
