package com.sharepast.its.common;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 7/14/11
 * Time: 10:53 PM
 * Fix for bug http://restlet.tigris.org/issues/show_bug.cgi?id=1287
 */
public class MyJsonRepresentation extends JsonRepresentation {

    public MyJsonRepresentation(JSONArray jsonArray) {
        super(jsonArray);
    }

    public MyJsonRepresentation(JSONObject jsonObject) {
        super(jsonObject);
    }

    public MyJsonRepresentation(JSONStringer jsonStringer) {
        super(jsonStringer);
    }

    public MyJsonRepresentation(JSONTokener jsonTokener) {
        super(jsonTokener);
    }

    public MyJsonRepresentation(Map<String, Object> map) {
        super(map);
    }

    public MyJsonRepresentation(Object bean) {
        super(bean);
    }

    public MyJsonRepresentation(Representation jsonRepresentation) throws IOException {
        super(jsonRepresentation);
    }

    public MyJsonRepresentation(String jsonString) {
        super(jsonString);
    }

    @Override
    public void write(Writer writer) throws IOException {
        super.write(writer);    //To change body of overridden methods use File | Settings | File Templates.
        //writer.flush();
    }
}
