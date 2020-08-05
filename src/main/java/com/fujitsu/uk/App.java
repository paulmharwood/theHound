package com.fujitsu.uk;

/**
 * Hello world!
 *
 */
import com.fujitsu.uk.markdown.MarkdownGen;

import java.io.File;

public class App {
    public static void main(String[] args) {
        if(args.length >= 1){
            File file;
            try{
                file =  new File(args[0]);
                String outputFile = file.getParent() + "/README.md";
                try{
                    outputFile = args[1];
                    System.out.println("Output path provided, so using " + outputFile);
                }catch(ArrayIndexOutOfBoundsException aiofbe){
                    System.out.println("No output file or path supplied so using default");
                }
                new MarkdownGen(outputFile).generateMarkdownForYamlFile(file);
                File readmeFile = new File(outputFile);
                if (readmeFile.exists()){
                    System.out.println("Successfully created README.md file " + readmeFile.getAbsolutePath());
                }
            }catch (NullPointerException npe){
                System.out.println("The Cloudformation file you have provided does not exist");
            }
        }else{
            System.out.println("You must provide a yaml Cloudformation template file as a parameter");
        }
    }
}