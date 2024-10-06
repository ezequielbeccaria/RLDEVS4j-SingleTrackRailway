/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rldevs4j.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rldevs4j.base.env.msg.Step;
import spark.ResponseTransformer;

/**
 *
 * @author ezequiel
 */
public class JsonTransformer implements ResponseTransformer {

    private ObjectMapper jsonMapper;

    public JsonTransformer() {
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.jsonMapper.setAnnotationIntrospector(new Step.IgnoreInheritedIntrospector());
    }

    @Override
    public String render(Object model) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(model);
    }

}
