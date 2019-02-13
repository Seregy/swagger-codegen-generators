package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenArgument;
import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenProperty;
import io.swagger.codegen.v3.CodegenType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class DefaultCodegenConfigTest {

    @Test
    public void testInitialValues() throws Exception {
        final DefaultCodegenConfig codegen = new P_DefaultCodegenConfig();
        codegen.processOpts();

        Assert.assertEquals(codegen.modelPackage, "");
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), null);
        Assert.assertEquals(codegen.apiPackage, "");
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), null);
        Assert.assertEquals(codegen.sortParamsByRequiredFlag, Boolean.TRUE);
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG), Boolean.TRUE);
        Assert.assertEquals(codegen.hideGenerationTimestamp, Boolean.TRUE);
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.TRUE);
    }

    @Test
    public void testSetters() throws Exception {
        final DefaultCodegenConfig codegen = new P_DefaultCodegenConfig();
        codegen.setModelPackage("xxx.yyyyy.zzzzzzz.model");
        codegen.setApiPackage("xxx.yyyyy.zzzzzzz.api");
        codegen.setSortParamsByRequiredFlag(false);
        codegen.setHideGenerationTimestamp(false);
        codegen.processOpts();

        Assert.assertEquals(codegen.modelPackage, "xxx.yyyyy.zzzzzzz.model");
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "xxx.yyyyy.zzzzzzz.model");
        Assert.assertEquals(codegen.apiPackage, "xxx.yyyyy.zzzzzzz.api");
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "xxx.yyyyy.zzzzzzz.api");
        Assert.assertEquals(codegen.sortParamsByRequiredFlag, Boolean.FALSE);
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG), Boolean.FALSE);
        Assert.assertEquals(codegen.hideGenerationTimestamp, Boolean.FALSE);
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
    }

    @Test
    public void testPutAdditionalProperties() throws Exception {
        final DefaultCodegenConfig codegen = new P_DefaultCodegenConfig();
        codegen.additionalProperties().put(CodegenConstants.MODEL_PACKAGE, "xx.yyyyy.model");
        codegen.additionalProperties().put(CodegenConstants.API_PACKAGE, "xx.yyyyy.api");
        codegen.additionalProperties().put(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG, false);
        codegen.additionalProperties().put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, false);
        codegen.processOpts();

        Assert.assertEquals(codegen.modelPackage, "xx.yyyyy.model");
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.MODEL_PACKAGE), "xx.yyyyy.model");
        Assert.assertEquals(codegen.apiPackage, "xx.yyyyy.api");
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.API_PACKAGE), "xx.yyyyy.api");
        Assert.assertEquals(codegen.sortParamsByRequiredFlag, Boolean.FALSE);
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG), Boolean.FALSE);
        Assert.assertEquals(codegen.hideGenerationTimestamp, Boolean.FALSE);
        Assert.assertEquals(codegen.additionalProperties().get(CodegenConstants.HIDE_GENERATION_TIMESTAMP), Boolean.FALSE);
    }

    @Test
    public void testNumberSchemaMinMax() {
        Schema schema = new NumberSchema()
                .minimum(BigDecimal.valueOf(50))
                .maximum(BigDecimal.valueOf(1000));

        final DefaultCodegenConfig codegen = new P_DefaultCodegenConfig();
        CodegenProperty codegenProperty = codegen.fromProperty("test", schema);

        Assert.assertEquals(codegenProperty.minimum, "50");
        Assert.assertEquals(codegenProperty.maximum, "1000");
    }

    @Test
    public void testFromOperation_BodyParamsUnique() {
        PathItem dummyPath = new PathItem()
            .post(new Operation())
            .get(new Operation());
      
        OpenAPI openAPI = new OpenAPI()
            .path("dummy", dummyPath);

        final DefaultCodegenConfig codegen = new P_DefaultCodegenConfig();
        codegen.setEnsureUniqueParams(false);
        final Operation operation = new Operation();

        RequestBody body = new RequestBody();
        body.setDescription("A list of list of values");
        body.setContent(new Content().addMediaType("application/json", new MediaType().schema(new ArraySchema().items(new ArraySchema().items(new IntegerSchema())))));
        operation.setRequestBody(body);
        Parameter param = new Parameter().in("query").name("testParameter");
        operation.addParametersItem(param);
        
        CodegenOperation codegenOperation = codegen.fromOperation("/path", "GET", operation, null, openAPI);

        Assert.assertEquals(true, codegenOperation.allParams.get(0).getVendorExtensions().get("x-has-more"));
        Assert.assertEquals(false, codegenOperation.bodyParams.get(0).getVendorExtensions().get("x-has-more"));

        codegenOperation.allParams.get(0).getVendorExtensions().put("x-has-more", false);
        codegenOperation.bodyParams.get(0).getVendorExtensions().put("x-has-more", true);

        Assert.assertEquals(false, codegenOperation.allParams.get(0).getVendorExtensions().get("x-has-more"));
        Assert.assertEquals(true, codegenOperation.bodyParams.get(0).getVendorExtensions().get("x-has-more"));
    }

    private static class P_DefaultCodegenConfig extends DefaultCodegenConfig{
        @Override
        public String getArgumentsLocation() {
            return null;
        }

        @Override
        public String getDefaultTemplateDir() {
            return null;
        }

        @Override
        public CodegenType getTag() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getHelp() {
            return null;
        }

        @Override
        public List<CodegenArgument> readLanguageArguments() {
            return null;
        }
    }
}
