package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import java.util.List;

public interface DataStorage {
	
	public List<String> list(String target);
	
	public int archive(String target, String dest);
	
	public boolean remove(String target, boolean recursive);
	
	public boolean exists(String target);
	
	public static String join(String pre, String post) {
		if(pre.endsWith("/") && post.startsWith("/")) {
			return pre.substring(0, pre.length() -1) + post;
		}
		else if(!pre.endsWith("/") && !post.startsWith("/")) {
			return pre + "/" + post;
		}
		else {
			return pre + post;
		}
	}
}
