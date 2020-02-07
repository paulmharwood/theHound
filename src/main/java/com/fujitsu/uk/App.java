package com.fujitsu.uk;

/**
 * Hello world!
 *
 */
import com.fujitsu.uk.markdown.MarkdownGen;

import java.io.File;

public class App {
    public static void main(String[] args) {
        if(args.length == 1){
            File file;
            try{
                file =  new File(args[0]);
                new MarkdownGen(file.getParent() + "/README.md").generateMarkdownForYamlFile(file);
                File readmeFile = new File(file.getParent() + "/README.md");
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