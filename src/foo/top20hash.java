package foo;

        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
        
public class top20hash{
	
	private static TreeMap<Text, LongWritable> countData = new TreeMap<Text, LongWritable>();
	
	public static class MapTask extends
	   Mapper<Text, LongWritable, NullWritable, Text> {
	  
		
		public void map(Text key, LongWritable value, Context context)
	    throws java.io.IOException, InterruptedException {
	   
	    String data = value.toString();
	   
	    Text hashtag = new Text (key);
	    LongWritable count = value;
	   
	    countData.put(hashtag,count);
	   }
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
	    Map<Text, LongWritable> sortedMap = sortByValues(countData);
	
	    int counter = 0;
	    for (Text key : sortedMap.keySet()) {
	        if (counter++ == 20) {
	            break;
	        }
	        context.write(key, sortedMap.get(key));
    }
	}

	 @SuppressWarnings("rawtypes")
	public static <K extends Comparable, V extends Comparable> Map<K, V> 
	 sortByValues(Map<K, V> map) {
	        List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
	                map.entrySet());
	 
	        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
	 
	            @SuppressWarnings("unchecked")
				@Override
	            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
	                return o2.getValue().compareTo(o1.getValue());
	            }
	        });
			return map;
	 }
	 
public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

   public java.util.Map<Text, IntWritable> countMap = new HashMap<>();
	   
   public void reduce(Text key, IntWritable values, Context context) 
     throws IOException, InterruptedException {
	   
       context.write(key, values);
            
}
} 
	 
	    public static void main(String[] args) throws Exception {
	        Configuration conf = new Configuration();
	        Job job = Job.getInstance(conf, "top hashtags");
	        job.setJarByClass(top20hash.class);
	        job.setMapperClass(Mapper.class);
	        
	        job.setMapOutputKeyClass(LongWritable.class);
	        job.setMapOutputValueClass(Text.class);
	        
	        // job.setCombinerClass(TopNCombiner.class);
	        job.setReducerClass(Reducer.class);
	        job.setOutputKeyClass(Text.class);
	        job.setOutputValueClass(IntWritable.class);
	        FileInputFormat.addInputPath(job, new Path(args[0]));
	        FileOutputFormat.setOutputPath(job, new Path(args[1]));
	        System.exit(job.waitForCompletion(true) ? 0 : 1);
	    }
}

