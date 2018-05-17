package com.example.hp.scissorsshop.DataClasses;

import org.json.JSONException;
import org.json.JSONObject;


public interface  Json<T> {
    /**
     *  converts implementing class object to json and return it.
     */
    JSONObject toJson() throws JSONException;

    /**
     *
     * @param j jsonObject from which attrs Object of implementing class set.
     *
     */

     void fromJson(JSONObject j);
}
