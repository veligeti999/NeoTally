package com.newtally.core.resource;

import com.newtally.core.util.JsonParser;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class AbstractResourceTest {

    private JsonParser parser = new JsonParser();

    protected <T>  T post(String path, T obj, String userName, String password) {
        Response res = getWebTarget(path, userName, password).post(
                Entity.entity(parser.toString(obj), MediaType.APPLICATION_JSON));

        String resStr = res.readEntity(String.class);

        if(res.getStatus() != HttpServletResponse.SC_OK) {
            throw new RuntimeException(resStr);
        } else {
            return parser.parseObject((Class<T>) obj.getClass(), resStr);
        }
    }

    protected <T>  void put(String path, T obj, String userName, String password) {
        Response res = getWebTarget(path, userName, password).put(
                Entity.entity(parser.toString(obj), MediaType.APPLICATION_JSON));

        String resStr = res.readEntity(String.class);

        if(res.getStatus() != HttpServletResponse.SC_OK) {
            throw new RuntimeException(resStr);
        }
    }

    protected <T>  T get(String path, Class<T> clazz, String userName, String password) {
        return parser.parseObject(clazz, _get(path, userName, password));
    }

    private String _get(String path, String userName, String password) {
        Response res = getWebTarget(path, userName, password).get();

        String resStr = res.readEntity(String.class);

        if(res.getStatus() != HttpServletResponse.SC_OK) {
            throw new RuntimeException(resStr);
        } else {
            return resStr;
        }
    }

    protected <T> List<T> getList(String path, Class<T> clazz, String userName, String password) {
        return parser.parseObjectAsList(clazz, _get(path, userName, password));
    }

    private Invocation.Builder getWebTarget(String path, String userName, String password) {

        ClientConfig clientConfig = new ClientConfig();

        if(userName != null) {
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName, password);
            clientConfig.register( feature) ;
        }

        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget target = client.target(getBasePath() + path);

        Invocation.Builder req = target.request(MediaType.APPLICATION_JSON);

        return req;
    }

    private String getBasePath() {
        String path = System.getProperty("NEW_TALLY_PATH");

        if(path == null) {
            return "http://localhost:8080/newtally/rest";
        } else {
            return path;
        }

    }

}
