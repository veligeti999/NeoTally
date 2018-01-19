package com.newtally.core.resource;

import com.blockcypher.utils.gson.GsonFactory;
import com.google.gson.Gson;

public class BaseResource {

    protected final Gson gson = GsonFactory.getGson();
    
    protected final Gson gson_pretty = GsonFactory.getGsonPrettyPrint();
}
