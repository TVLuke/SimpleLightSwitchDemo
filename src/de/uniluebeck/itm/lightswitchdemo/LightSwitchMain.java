/*
 * Copyright (C) Institute of Telematics, Lukas Ruge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uniluebeck.itm.lightswitchdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * The Simple Liughtswitch demo is created to show how to potentially use the DynamixBridge 
 * to request configured context updates to control actors in space
 * 
 * @author lukas
 *
 */
public class LightSwitchMain 
{

	static String command="";
	
	/**
	 * @param args the ip and a command
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		if(args.length>0)
		{
			String url = args[0];
			if(args.length>1)
			{
				command = args[1];
				new LightSwitchMain(url, command);
			}
			else
			{
				new LightSwitchMain(url, "");
			}

	    	
		}
	}
	
	public LightSwitchMain(String ip, String command)
	{
		System.out.println(ip);
		System.out.println(command);
		while(true)
		{
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			try 
			{
				doc = builder.build(ip);
		    	Element root = doc.getRootElement();
		    	root.getAttributes();
		    	List<Element> children = root.getChildren();
				Iterator<Element> childrenIterator = children.iterator();
				
				while(childrenIterator.hasNext())
				{
					Element child = childrenIterator.next(); 
					List<Element> grandchildren = child.getChildren();
					Iterator<Element> grandchildrenIterator = grandchildren.iterator();
					boolean x=false;
					boolean a=false;
					while(grandchildrenIterator.hasNext())
					{
						Element grandchild = grandchildrenIterator.next();
						System.out.println(grandchild.getName());
						if(grandchild.getName().equals("name"))
						{
							System.out.println(grandchild.getText());
							if(grandchild.getText().equals("org.ambientdynamix.contextplugins.artnet"))
							{
								x=true;
							}
						}
						if(x && grandchild.getName().equals("active"))
						{
							if(grandchild.getText().equals("true"))
							{
								a=true;
							}
							if(a)
							{
								//The plugin can be used. however, this does not happen here but rather in the url field.
							}
							else
							{
								//we have to request the plugin
								URI uri = null;
								try 
								{
									uri = new URI(ip);
								} 
								catch (URISyntaxException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
								conn.setDoOutput(true);
								conn.setRequestMethod("PUT");
								conn.addRequestProperty("format", "xml");
								String payload="org.ambientdynamix.contextplugins.artnet";
								conn.getOutputStream().write(payload.getBytes());
								BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
							 
								String output;
								System.out.println("Output from Server .... \n");
								while ((output = br.readLine()) != null) 
								{
									System.out.println(output);
								}
								conn.disconnect();
							}
						}
						if(x && a && grandchild.getName().equals("url"))
						{
							List<Element> ggclist = grandchild.getChildren();
							Iterator<Element> ggclistIterator = ggclist.iterator();
							while(ggclistIterator.hasNext())
							{
								Element ggc = ggclistIterator.next();
								if(ggc.getName().equals("http"))
								{
									String contexturl = ggc.getText();
									System.out.println(contexturl);
									URI uri = null;
									try 
									{
										uri = new URI(contexturl);
									} 
									catch (URISyntaxException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
									conn.setDoOutput(true);
									conn.setRequestMethod("POST");
									conn.addRequestProperty("format", "xml");
									Random generator = new Random();
									int rr = generator.nextInt(255);
									int gg = generator.nextInt(255);
									int bb = generator.nextInt(255);
									System.out.println(rr+" "+gg+" "+bb);
									String payload="String action_type=setcolor;;String r_channel="+rr+";;String g_channel="+gg+";;String b_channel="+bb+"";
									conn.getOutputStream().write(payload.getBytes());
									new InputStreamReader((conn.getInputStream()));
								 
									String output;
									System.out.println("Output from Server .... \n");
									conn.disconnect();
								}
							}
							
						}
						
					}
					System.out.println(child.getName());
				}
			} 
			catch (JDOMException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try 
			{
				System.out.println("sleep");
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
