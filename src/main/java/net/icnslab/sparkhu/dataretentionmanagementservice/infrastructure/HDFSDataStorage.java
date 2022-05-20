package net.icnslab.sparkhu.dataretentionmanagementservice.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.fs.HarFileSystem;
import org.apache.hadoop.tools.HadoopArchives;
import org.apache.hadoop.util.ToolRunner;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.icnslab.sparkhu.dataretentionmanagementservice.domain.DataStorage;

@Component
public class HDFSDataStorage implements DataStorage{
	
	@Value("${storage.scheme}")
	private String scheme;
	
	@Value("${storage.host}")
	private String host;
	
	@Value("${storage.port}")
	private String port;
	
	@Value("${storage.username}")
	private String username;
	
	private String url;
	
	private static FileSystem fileSystem;
	
	public HDFSDataStorage() {
		buildUrl();
		try {
			fileSystem = FileSystem.get(new URI(url), new Configuration(), username);
		} catch(Exception e) {
			System.out.println("Error HDFS Connection failed");
		}
	}
	
	public List<String> list(String target){
		List<String> ret = new ArrayList<>();
		try {
			RemoteIterator<LocatedFileStatus> fileStatusIterator = fileSystem.listFiles(new Path(target), false);
			while(fileStatusIterator.hasNext()) {
				LocatedFileStatus fileStatus = fileStatusIterator.next();
				ret.add(fileStatus.getPath().toString());
			}
			
		} catch(Exception e) {
			System.out.println("Error: Can't listing in the path");
		}
		return ret;
	}
	
	
	public int archive(String target, String dest) {
		JobConf job = new JobConf(HadoopArchives.class);
		HadoopArchives har = new HadoopArchives(job);
		String args[] = new ArchiveArgsFactory(target, dest).build().toArray(String[]::new);
		int ret = 0;
		try{
			ret = ToolRunner.run(har, args);
		} catch(Exception e) {
		      System.err.println("Error in HDFS archive" + e.getClass().getSimpleName());
		      final String s = e.getLocalizedMessage();
		      if (s != null) {
		        System.err.println(s);
		      } else {
		        e.printStackTrace(System.err);
		      }
		}
		if (ret == 0) {
			ret = remove(target, true) ? 0 : 1;
		}
		return ret;
	}
	
	
	public boolean remove(String target, boolean recursive) {
		boolean ret = false;
		try {
			ret = fileSystem.delete(new Path(target), recursive);
			
		} catch(Exception e) {
			System.err.println("Error in HDFS remove" + e.getClass().getSimpleName());
		    final String s = e.getLocalizedMessage();
		    if (s != null) {
		        System.err.println(s);
		    } else {
		       e.printStackTrace(System.err);
		    }
		}
		return ret;
	}
	
	public boolean exists(String target) {
		boolean ret = false;
		try {
			ret = fileSystem.exists(new Path(target));
			
		} catch(Exception e) {
			System.err.println("Error in HDFS check existence" + e.getClass().getSimpleName());
		    final String s = e.getLocalizedMessage();
		    if (s != null) {
		        System.err.println(s);
		    } else {
		       e.printStackTrace(System.err);
		    }
		}
		return ret;
	}
	
	private void buildUrl() {
        url = scheme + "://" + host + ":" + port;
    }
	
	static class ArchiveArgsFactory{
		
		private String src;
		private String dest;
		
		public ArchiveArgsFactory(String src, String dest){
			this.src = src; 
			this.dest = dest;
		}
		Stream<String> build() {
			// https://hadoop.apache.org/docs/stable/hadoop-archives/HadoopArchives.html
			ArrayList<String> list = new ArrayList<String> ();
		    list.add("-archiveName");
		    list.add(buildHar());
			list.add("-p");
			list.add(src);
			list.add(dest);
			return list.stream();
		}
		
		private String buildHar() {
			String[] tokens = src.split("/");
			String sourceName = tokens[1];
			String tableName = tokens[2];
			String year = tokens[3].substring("year=".length());
			String month = tokens[4].substring("month=".length());
			String day = tokens[5].substring("day=".length());
			StringJoiner joiner = new StringJoiner("-");
			joiner.add(sourceName); joiner.add(tableName); joiner.add(year); joiner.add(month); joiner.add(day); 
			return joiner.toString() + ".har";
		}
	}
}
