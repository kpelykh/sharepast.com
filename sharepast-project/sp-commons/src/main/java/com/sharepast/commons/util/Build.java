package com.sharepast.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Build {
    private static final Logger LOG = LoggerFactory.getLogger(Build.class);

    private static boolean isLoaded = false;

    public static final String[] modules = new String[]
            {
                      "sp-config"
                    , "sp-container"
                    , "sp-dal"
                    , "sp-core"
                    , "sp-commons"
                    , "sp-jms"
                    , "sp-service"
                    , "sp-db-changes"
            };

    public static List<ComponentInfo> components = new ArrayList<ComponentInfo>(modules.length);

    public static List<ComponentInfo> getComponents() {
        try {
            read();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return components;
    }

    protected static void read()
            throws IOException {
        if (isLoaded)
            return;

        for (String module : modules) {
            String version = "";
            InputStream is = Build.class.getResourceAsStream( String.format("/com/sharepast/%s/pom.properties", module) );
            if (is != null)  {
                BufferedReader r = new BufferedReader(new InputStreamReader(is, Charset.forName("utf8")));

                String line;

                while ((line = r.readLine()) != null) {
                    if (line.startsWith("version")) {
                        version = line.substring(8);
                        break;
                    }
                }

                r.close();
            }

            GitRepositoryState grs = getGitRepositoryState(module);
            components.add(new ComponentInfo(module, version, grs));
        }

        isLoaded = true;
    }

    public static class ComponentInfo {
        String name;
        String version;
        GitRepositoryState gitState;

        public ComponentInfo(String name, String version, GitRepositoryState gitState) {
            this.name = name;
            this.version = version;
            this.gitState = gitState;
        }

        public ComponentInfo(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public GitRepositoryState getGitState() {
            return gitState;
        }

        @Override
        public String toString() {
            if ( gitState!= null ) {
                return "ComponentInfo{" +
                        "name='" + name + '\'' +
                        ", version='" + version + '\'' +
                        ", git hash=" + gitState.getCommitId() + '\'' +
                        '}';
            } else {
                return "ComponentInfo{" +
                        "name='" + name + '\'' +
                        ", version='" + version + '\'' +
                        '}';

            }
        }
    }

    private static GitRepositoryState getGitRepositoryState(String module) throws IOException
    {

        Properties properties = new Properties();
        InputStream is = Build.class.getResourceAsStream(String.format( "/com/sharepast/%s/git.properties", module));
        if (is != null) {
            properties.load(is);
            return new GitRepositoryState(properties);
        } else {
            return null;
        }


    }
}
