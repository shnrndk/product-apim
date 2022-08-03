package org.wso2.am.integration.test.impl;

import org.wso2.am.integration.clients.gateway.api.ApiClient;
import org.wso2.am.integration.clients.gateway.api.ApiException;
import org.wso2.am.integration.clients.gateway.api.auth.HttpBasicAuth;
import org.wso2.am.integration.clients.gateway.api.v2.DeployApiApi;
import org.wso2.am.integration.clients.gateway.api.v2.GetApiArtifactsApi;
import org.wso2.am.integration.clients.gateway.api.v2.GetApiInfoApi;
import org.wso2.am.integration.clients.gateway.api.v2.GetApplicationInfoApi;
import org.wso2.am.integration.clients.gateway.api.v2.GetSubscriptionInfoApi;
import org.wso2.am.integration.clients.gateway.api.v2.UndeployApiApi;
import org.wso2.am.integration.clients.gateway.api.v2.dto.APIArtifactDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.APIInfoDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.ApplicationInfoDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.ApplicationListDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.EndpointsDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.LocalEntryDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.SequencesDTO;
import org.wso2.am.integration.clients.gateway.api.v2.dto.SubscriptionDTO;
import org.wso2.am.integration.test.utils.APIManagerIntegrationTestException;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class RestAPIGatewayImpl {
    private String tenantDomain = null;
    GetApiArtifactsApi getApiArtifactsApi = new GetApiArtifactsApi();
    DeployApiApi deployApiApi = new DeployApiApi();
    UndeployApiApi undeployApiApi = new UndeployApiApi();
    GetApiInfoApi apiInfoApi = new GetApiInfoApi();
    GetApplicationInfoApi applicationInfoApi = new GetApplicationInfoApi();
    GetSubscriptionInfoApi subscriptionInfoApi = new GetSubscriptionInfoApi();

    public RestAPIGatewayImpl(String username, String password, String tenantDomain) {
        this(username, password, tenantDomain, "https://localhost:9443/");
    }

    public RestAPIGatewayImpl(String username, String password, String tenantDomain, String gatewayURL) {
        ApiClient apiClient = new ApiClient();
        String basicEncoded =
                DatatypeConverter.printBase64Binary((username + ':' + password).getBytes(StandardCharsets.UTF_8));
        apiClient.addDefaultHeader("Authorization", "Basic " + basicEncoded);
        apiClient.setDebugging(true);
        apiClient.setBasePath(gatewayURL + "/api/am/gateway/v2");
        apiClient.setReadTimeout(600000);
        apiClient.setConnectTimeout(600000);
        apiClient.setWriteTimeout(600000);
        apiClient.setVerifyingSsl(false);
        getApiArtifactsApi.setApiClient(apiClient);
        deployApiApi.setApiClient(apiClient);
        undeployApiApi.setApiClient(apiClient);
        apiInfoApi.setApiClient(apiClient);
        applicationInfoApi.setApiClient(apiClient);
        subscriptionInfoApi.setApiClient(apiClient);
        this.tenantDomain = tenantDomain;
    }

    public APIArtifactDTO retrieveAPI(String name, String version) throws ApiException {
        return getApiArtifactsApi.apiArtifactGet(name, version, tenantDomain);
    }

    public EndpointsDTO retrieveEndpoints(String name, String version) throws ApiException {
        return getApiArtifactsApi.endPointsGet(name, version, tenantDomain);
    }

    public LocalEntryDTO retrieveLocalEntries(String name, String version) throws ApiException {
        return getApiArtifactsApi.localEntryGet(name, version, tenantDomain);
    }

    public SequencesDTO retrieveSequences(String name, String version) throws ApiException {
        return getApiArtifactsApi.sequenceGet(name, version, tenantDomain);
    }

    public SubscriptionDTO retrieveSubscription(String apiId, String applicationId) throws APIManagerIntegrationTestException {
        try {
            return subscriptionInfoApi.subscriptionsGet(apiId, applicationId, tenantDomain);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return null;
            } else {
                throw new APIManagerIntegrationTestException(e);
            }
        }
    }

    public ApplicationInfoDTO retrieveApplication(String applicationId) throws APIManagerIntegrationTestException {

        try {
            ApplicationListDTO applicationListDTO = applicationInfoApi.applicationsGet(null, applicationId,
                    tenantDomain);
            if (applicationListDTO != null && applicationListDTO.getList() != null) {
                for (ApplicationInfoDTO applicationInfoDTO : applicationListDTO.getList()) {
                    if (applicationInfoDTO.getUuid().equals(applicationId)) {
                        return applicationInfoDTO;
                    }
                }
            }

        } catch (ApiException e) {
            throw new APIManagerIntegrationTestException(e);
        }
        return null;
    }

    public APIInfoDTO getAPIInfo(String apiId) throws APIManagerIntegrationTestException {

        try {
            return apiInfoApi.apisApiIdGet(apiId, tenantDomain);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return null;
            }else{
                throw new APIManagerIntegrationTestException(e);
            }
        }
    }
}
