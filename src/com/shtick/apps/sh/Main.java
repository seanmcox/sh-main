/**
 * 
 */
package com.shtick.apps.sh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

/**
 * @author Sean
 *
 */
public class Main {
    private static Framework framework = null;
    private static Object mainService;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        // Print welcome banner.
        System.out.println("\nSH Initializing");
        System.out.println("======================\n");

        try{
            framework = getFrameworkFactory().newFramework(null);
            framework.init();
            Map<String,String> config=new HashMap<String,String>();
            // Configure auto-deploy of bundles.
            config.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERTY, AutoProcessor.AUTO_DEPLOY_INSTALL_VALUE+","+AutoProcessor.AUTO_DEPLOY_UPDATE_VALUE+","+AutoProcessor.AUTO_DEPLOY_START_VALUE);
            
            for(String key:config.keySet())
            	System.getProperties().setProperty(key, config.get(key));
            
            AutoProcessor.process(config, framework.getBundleContext());
            framework.start();
            ServiceReference<?> driverReference=framework.getBundleContext().getServiceReference("com.shtick.apps.sh.core.Driver");
            ServiceReference<?> guiReference=framework.getBundleContext().getServiceReference("com.shtick.apps.sh.core.UIDriver");
            if(guiReference==null){
            	System.out.println("No MainService found.");
            	synchronized(config){
            		config.wait(30000);
            	}
            }
            else if(driverReference==null){
            	System.out.println("No driver found.");
            	synchronized(config){
            		config.wait(30000);
            	}
            }
            else{
        		Object driver=framework.getBundleContext().getService(driverReference);
        		mainService=framework.getBundleContext().getService(guiReference);
        		Method[] methods = mainService.getClass().getMethods();
        		boolean found=false;
        		for(Method method:methods){
        			Class<?>[] parameterTypes = method.getParameterTypes();
        			if(parameterTypes.length!=1)
        				continue;
        			if("com.shtick.apps.sh.core.Driver".equals(parameterTypes[0].getName())){
        				found = true;
                    	mainService.getClass().getMethod("main",driver.getClass().getInterfaces()[0]).invoke(mainService, driver);
                    	break;
        			}
        		}
        		if(!found)
        			throw new RuntimeException("Expected main method not found in Main Service.");
            }
        	framework.stop();
            System.exit(0);
        }
        catch (Exception t){
            System.err.println("Could not create framework: " + t);
            t.printStackTrace();
            System.exit(-1);
        }
    }

    private static FrameworkFactory getFrameworkFactory() throws Exception{
        java.net.URL url = Main.class.getClassLoader().getResource(
            "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url != null){
            try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))){
                for (String s = br.readLine(); s != null; s = br.readLine()){
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#'))
                        return (FrameworkFactory) Class.forName(s).newInstance();
                }
            }
        }

        throw new Exception("Could not find framework factory.");
    }
}
