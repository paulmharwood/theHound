package com.fujitsu.uk.markdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.fujitsu.uk.utilities.Constants.*;

public class MarkdownGen {

    private String readmeFilePath;

    public MarkdownGen(String readmeFilePath){
        this.readmeFilePath = readmeFilePath;
    }

    public void generateMarkdownForYamlFile(File yaml) {

        try {

            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            HashMap obj = yamlReader.readValue(yaml, HashMap.class);

            writeMarkdownLineToFile("# " + yaml.getName());

            if(obj.containsKey(DESCRIPTION)) {
                processDescription((String)obj.get(DESCRIPTION));
            }

            if(obj.containsKey(METADATA)) {
                HashMap<String, HashMap> metadata = (HashMap) obj.get(METADATA);
                if(metadata.size() > 0){
                    processMetadata(metadata);
                }
            }

            if(obj.containsKey(PARAMETERS)) {
                HashMap<String, HashMap> parameters = (HashMap) obj.get(PARAMETERS);
                if(parameters.size() > 0){
                    processParameters(parameters);
                }
            }

            if(obj.containsKey(RESOURCES)) {
                HashMap<String, HashMap> resources = (HashMap) obj.get(RESOURCES);
                if(resources.size() > 0){
                    processResources(resources);
                }
            }

            if(obj.containsKey(OUTPUTS)) {
                HashMap<String, HashMap> outputs = (HashMap) obj.get(OUTPUTS);
                if(outputs.size() > 0){
                    processOutputs(outputs);
                }
            }

        } catch (IOException e) {
            System.out.println("The Cloudformation yaml template provided has not been found or is invalid");
        }finally {

            try {
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void processOutputs(HashMap<String, HashMap> outputs) {
        writeMarkdownLineToFile("## Outputs");
        writeMarkdownLineToFile(" Logical Name | Attribute ");
        writeMarkdownLineToFile(" --- | --- ");

        for(Map.Entry<String, HashMap> outputsEntry: outputs.entrySet()){

            HashMap<String, String> value = outputsEntry.getValue();
            String tableEntry = outputsEntry.getKey();

            for (Map.Entry<String, String> outputInfo : value.entrySet()) {
                switch (outputInfo.getKey()) {
                    case "Value":
                        if(outputInfo.getValue() instanceof String){
                            tableEntry += "|" + outputInfo.getValue();
                        }else{
                            tableEntry += "| !Ref ";
                        }
                        break;
                }
            }

            writeMarkdownLineToFile(tableEntry);

        }
    }

    private void processResources(HashMap<String, HashMap> resources) {
        writeMarkdownLineToFile("## Resources");
        writeMarkdownLineToFile(" Logical Name | Type ");
        writeMarkdownLineToFile(" --- | --- ");

        for(Map.Entry<String, HashMap> resourceEntry: resources.entrySet()){

            HashMap<String, String> value = resourceEntry.getValue();
            String tableEntry = resourceEntry.getKey();

                //writeMarkdownLineToFile(resourceEntry.getKey());
                for (Map.Entry<String, String> resourceInfo : value.entrySet()) {
                    switch (resourceInfo.getKey()) {
                        case "Type":
                            tableEntry += tableEntry + "|" + resourceInfo.getValue();
                            break;
                    }
                }

                writeMarkdownLineToFile(tableEntry);

        }
    }

    private void processMetadata(HashMap<String, HashMap> metadata) {
        for(Map.Entry<String, HashMap> metadataEntry: metadata.entrySet()){

            HashMap<String, String> value = metadataEntry.getValue();

            if(isRequiredMetadata(metadataEntry.getKey())) {

                writeMarkdownLineToFile("## " + metadataEntry.getKey());
                for (Map.Entry<String, String> metadataInfo : value.entrySet()) {
                    switch (metadataInfo.getKey()) {
                        case "Description":
                            writeMarkdownLineToFile(metadataInfo.getValue());
                            break;
                    }
                }
            }
        }
    }

    private boolean isRequiredMetadata(String key){
        boolean required = true;
        if(key.equalsIgnoreCase("AWS::CloudFormation::Init")
                || key.equalsIgnoreCase("AWS::CloudFormation::Interface")
                || key.equalsIgnoreCase("AWS::CloudFormation::Designer"))
            required = false;
        return required;
    }

    private void processDescription(String description) {
        writeMarkdownLineToFile("## About");
        writeMarkdownLineToFile(description);
    }

    private ParametersRequired areParametersRequired(HashMap<String, HashMap> parameters){
        ParametersRequired parametersRequired = new ParametersRequired();

        for(Map.Entry<String, HashMap> parameter: parameters.entrySet()) {
            HashMap<String, String> value = parameter.getValue();
            for (Map.Entry<String, String> paramInfo : value.entrySet()) {

                switch (paramInfo.getKey()) {
                    case DESCRIPTION:
                        parametersRequired.includeDescription = true;
                        break;
                    case PARM_ALLOWEDPATTERN:
                        parametersRequired.includeAllowedPattern = true;
                        break;
                    case PARM_ALLOWEDVALUES:
                        parametersRequired.includeAllowedValues = true;
                        break;
                    case PARM_CONSTRAINTDESCRIPTION:
                        parametersRequired.includeConstraintDescription = true;
                        break;
                    case PARM_DEFAULT:
                        parametersRequired.includeDefault = true;
                        break;
                    case PARM_MINLENGTH:
                        parametersRequired.includeMinLength = true;
                        break;
                    case PARM_MAXLENGTH:
                        parametersRequired.includeMaxLength = true;
                        break;
                    case PARM_MINVALUE:
                        parametersRequired.includeMinValue = true;
                        break;
                    case PARM_MAXVALUE:
                        parametersRequired.includeMaxValue = true;
                        break;
                    case PARM_NOECHO:
                        parametersRequired.includeNoEcho = true;
                        break;
                }
            }
        }

        return parametersRequired;
    }

    private void processParameters(HashMap<String, HashMap> parameters) {
        writeMarkdownLineToFile("## Parameters");

        ParametersRequired parametersRequired = areParametersRequired(parameters);

        ArrayList<String> parameterEntries = new ArrayList<>();
        for(Map.Entry<String, HashMap> parameter: parameters.entrySet()){
            String name = parameter.getKey();
            String description = "";
            String type = "";
            String allowedPattern = "";
            String allowedValues = "";
            String constraintDescription = "";
            String _default = "";
            String minLength = "";
            String maxLength = "";
            String minValue = "";
            String maxValue = "";
            String noEcho = "";
            HashMap<String, String> value = parameter.getValue();
            for(Map.Entry<String, String> paramInfo : value.entrySet()){

                switch(paramInfo.getKey()){
                    case DESCRIPTION:
                        description = paramInfo.getValue().replace("|", "&#124;");
                        break;
                    case TYPE:
                        type = paramInfo.getValue().replace("<", " ").replace(">", "");
                        break;
                    case PARM_ALLOWEDPATTERN:
                        allowedPattern = paramInfo.getValue().replace("|", "&#124;");
                        break;
                    case PARM_ALLOWEDVALUES:
                        Object x = paramInfo.getValue();
                        allowedValues = x.toString().replace("[","").replace("]","");
                        break;
                    case PARM_CONSTRAINTDESCRIPTION:
                        constraintDescription = paramInfo.getValue().replace("|", "&#124;");
                        break;
                    case PARM_DEFAULT:
                        _default = paramInfo.getValue().replace("|", "&#124;");
                        break;
                    case PARM_MINLENGTH:
                        minLength = String.valueOf(paramInfo.getValue());
                        break;
                    case PARM_MAXLENGTH:
                        maxLength = String.valueOf(paramInfo.getValue());
                        break;
                    case PARM_MINVALUE:
                        minValue = String.valueOf(paramInfo.getValue());
                        break;
                    case PARM_MAXVALUE:
                        maxValue = String.valueOf(paramInfo.getValue());
                        break;
                    case PARM_NOECHO:
                        noEcho = String.valueOf(paramInfo.getValue());
                        break;
                }
            }
            parameterEntries.add(name + "|"
                    + type
                    + (parametersRequired.includeDescription == true ? "|" + description : "")
                    + (parametersRequired.includeAllowedPattern == true ? "|" + allowedPattern : "")
                    + (parametersRequired.includeAllowedValues == true ? "|" + allowedValues : "")
                    + (parametersRequired.includeMinLength == true ? "|" + minLength : "")
                    + (parametersRequired.includeMaxLength == true ? "|" + maxLength : "")
                    + (parametersRequired.includeMinValue == true ? "|" + minValue : "")
                    + (parametersRequired.includeMaxValue == true ? "|" + maxValue : "")
                    + (parametersRequired.includeNoEcho == true ? "|" + noEcho : "")
                    + (parametersRequired.includeDefault == true ? "|" + _default : "")
                    + (parametersRequired.includeConstraintDescription == true ? "|" + constraintDescription : ""));
        }

        writeMarkdownLineToFile("Name | Type "
                + (parametersRequired.includeDescription == true ? " | Description " : "")
                + (parametersRequired.includeAllowedPattern == true ? " | Allowed Pattern " : "")
                + (parametersRequired.includeAllowedValues == true ? " | Allowed Values " : "")
                + (parametersRequired.includeMinLength == true ? " | Min Length " : "")
                + (parametersRequired.includeMaxLength == true ? " | Max Length " : "")
                + (parametersRequired.includeMinValue == true ? " | Min Value " : "")
                + (parametersRequired.includeMaxValue == true ? " | Max Value " : "")
                + (parametersRequired.includeNoEcho == true ? " | No Echo " : "")
                + (parametersRequired.includeDefault == true ? " | Default " : "")
                + (parametersRequired.includeConstraintDescription == true ? " | Constraint Description " : ""));

        writeMarkdownLineToFile("--- | --- "
                + (parametersRequired.includeDescription == true ? " | --- " : "")
                + (parametersRequired.includeAllowedPattern == true ? " | --- " : "")
                + (parametersRequired.includeAllowedValues == true ? " | --- " : "")
                + (parametersRequired.includeMinLength == true ? " | --- " : "")
                + (parametersRequired.includeMaxLength == true ? " | --- " : "")
                + (parametersRequired.includeMinValue == true ? " | --- " : "")
                + (parametersRequired.includeMaxValue == true ? " | --- " : "")
                + (parametersRequired.includeNoEcho == true ? " | --- " : "")
                + (parametersRequired.includeDefault == true ? " | --- " : "")
                + (parametersRequired.includeConstraintDescription == true ? " | --- " : ""));

        for(String i: parameterEntries){
            writeMarkdownLineToFile(i);
        }
    }


    BufferedWriter writer = null;
    private void writeMarkdownLineToFile(String markdownEntry){
        try{
            if(writer == null){
                writer = new BufferedWriter(new FileWriter(readmeFilePath));
                writer.write(markdownEntry);
            }else{
                writer.newLine();
                writer.append(markdownEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

