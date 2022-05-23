package net.icnslab.sparkhu.dataretentionmanagementservice.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.tools.HadoopArchives;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
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
	
	@Value("${yarn.resourcemanager.address}")
	private String addrResourceManager;
	
	@Value("${yarn.resourcemanager.scheduler.address}")
	private String addrResourceManagerScheduler;
	
	@Value("${mapreduce.jobhistory.address}")
	private String addrHistory;
	
	@Value("${mapreduce.framework.name}")
	private String mapreduceFrameworkName;
	
	@Value("${hadoop.home}")
	private String HADOOP_HOME;
	
	@Value("${hadoop.user.name}")
	private String HADOOP_USER_NAME;
	
	private FileSystem fileSystem;
	
	private FileSystem getOrCreateFileSystem() {
		if (fileSystem == null) {
			buildUrl();
			try {
				
				fileSystem = FileSystem.get(new URI(url), new Configuration(), username);
			} catch(Exception e) {
			      System.err.println("Error HDFS Connection failed" + e.getClass().getSimpleName());
			      final String s = e.getLocalizedMessage();
			      if (s != null) {
			        System.err.println(s);
			      } else {
			        e.printStackTrace(System.err);
			      }
			}
		}
		return fileSystem;
	}
	
	public ArrayList<String> list(String target){
		ArrayList<String> ret = new ArrayList<>();
		try {
			FileSystem fs = getOrCreateFileSystem();
			RemoteIterator<LocatedFileStatus> fileStatusIterator = fs.listLocatedStatus(new Path(target));
			while(fileStatusIterator.hasNext()) {
				LocatedFileStatus fileStatus = fileStatusIterator.next();
				ret.add(fileStatus.getPath().toString());
			}
			
		} catch(Exception e) {
		      System.err.println("Error in HDFS listing" + e.getClass().getSimpleName());
		      final String s = e.getLocalizedMessage();
		      if (s != null) {
		        System.err.println(s);
		      } else {
		        e.printStackTrace(System.err);
		      }
		}
		return ret;
	}
	
	public ArrayList<String> getGlobPaths(String path){
		ArrayList<String> ret = new ArrayList<>();
		try {
			FileSystem fs = getOrCreateFileSystem();
			FileStatus[] fileStatuses = fs.globStatus(new Path(path));
			for(FileStatus fileStatus: fileStatuses) {
				ret.add(fileStatus.getPath().toString());
			}
		} catch(Exception e) {
		      System.err.println("Error in HDFS getGlobPaths " + e.getClass().getSimpleName());
		      final String s = e.getLocalizedMessage();
		      if (s != null) {
		        System.err.println(s);
		      } else {
		        e.printStackTrace(System.err);
		      }
		}
		return ret;
	}
	
	public int archive(String target, String dest, boolean removeSrc) {
		buildUrl();
		JobConf job = new JobConf(HadoopArchives.class);
		job.set("yarn.resourcemanager.address", addrResourceManager); 
		job.set("yarn.resourcemanager.scheduler.address", addrResourceManagerScheduler);
		job.set("mapreduce.jobhistory.address", addrHistory);
		job.set("mapreduce.framework.name", mapreduceFrameworkName);
		job.set("fs.defaultFS", url);
		System.setProperty("HADOOP_USER_NAME", HADOOP_USER_NAME);
		job.set ("mapreduce.app-submission.cross-platform", "true");
		job.set ("mapreduce.app-submission.cross-platform", "true");
		job.set("yarn.application.classpath",
	              ",${HADOOP_HOME}/share/hadoop/common/*,${HADOOP_HOME}/share/hadoop/common/lib/*,"
	                  + "${HADOOP_HOME}/share/hadoop/hdfs/*,${HADOOP_HOME}/share/hadoop/hdfs/lib/*,"
	                  + "${HADOOP_HOME}/share/hadoop/mapreduce/*,${HADOOP_HOME}/share/hadoop/mapreduce/lib/*,"
	                  + "${HADOOP_HOME}/share/hadoop/yarn/*,${HADOOP_HOME}/share/hadoop/yarn/lib/*");
		HadoopArchives har = new HadoopArchives(job);
		String args[] = new ArchiveArgsFactory(target, dest).build().toArray(String[]::new);
		int a = 0;
		int ret = 0;
		try{
			ret = ToolRunner.run(har, args);
		} catch(Exception e) {
		      final String s = e.getLocalizedMessage();
		      if (s != null) {
		      } else {
		        e.printStackTrace(System.err);
		      }
		}
		if (ret == 0 && removeSrc) {
			ret = remove(target, true) ? 0 : 1;
		}
		return ret;
	}
	
	
	public boolean remove(String target, boolean recursive) {
		boolean ret = false;
		try {
			ret = getOrCreateFileSystem().delete(new Path(target), recursive);
			
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
			ret = getOrCreateFileSystem().exists(new Path(target));
			
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
	
	class ArchiveArgsFactory{
		
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
			list.add(url + new Path(src).toString());
			list.add(url + dest);
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
