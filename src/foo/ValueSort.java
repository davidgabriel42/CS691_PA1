package foo;

import java.io.IOException;
import java.util.*;
        

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.IntWritable.Comparator;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.NullWritable;


public class ValueSort{
	

	 public static class MapTask extends
	   Mapper<Text, LongWritable, NullWritable, Text> {
	  
		 private TreeMap<LongWritable, Text> countData = new TreeMap<LongWritable, Text>();

		 
		 public void map(Text key, LongWritable value, Context context)
	    throws java.io.IOException, InterruptedException {
	   
	   String data = value.toString();
	   
	   Text hashtag = new Text (key);
	   LongWritable count = value;
	   
	   countData.put(count, hashtag);
	   if(countData.size() > 20) {
		   countData.remove(countData.firstKey());
	   }
	   
	  }
	 //}
	 
	 
	 @Override
	 protected void cleanup(Context context) throws IOException, InterruptedException {
	 for (Map.Entry<LongWritable, Text> entry : countData.entrySet()) {
		 
	//	 context.write(NullWritable.get() , entry.getValue());
		 

	        for ( Text hashtags : countData.values() ) {
	            context.write(NullWritable.get(), hashtags);
	        }
	 }
	 
	 }
	 }
/*
	 protected void cleanup(Context context) throws IOException, InterruptedException {
	 for (Map.Entry<LongWritable, Text> entry : countData.entrySet()) {
	 context.write(NullWritable.get(), entry.getValue());
	 }
	*/ 
	 public static class ReduceTask extends
	   Reducer<NullWritable, Text, NullWritable, Text> {
	  public void reduce(NullWritable key, Iterable<Text> values, Context context)
	    throws java.io.IOException, InterruptedException {
	   
	   for (Text value : values) {
	    
	    context.write(key,value);
	    
	   }
	   
	  }
	 }

	public static void main(String[] args) throws Exception {
	 Configuration conf = new Configuration();
	     
	     Job job = new Job(conf, "valsort");
	 
	 job.setOutputKeyClass(LongWritable.class);
	 job.setOutputValueClass(Text.class);
	     
	 //job.setMapperClass(Map.class);
	 //job.setReducerClass(Reduce.class);
	     
	 job.setInputFormatClass(TextInputFormat.class);
	 job.setOutputFormatClass(TextOutputFormat.class);
	     
	 FileInputFormat.addInputPath(job, new Path(args[0]));
	 FileOutputFormat.setOutputPath(job, new Path(args[1]));
	 
	 job.setJarByClass(ValueSort.class);
	 
	 job.waitForCompletion(true);
	 }
	     



 public static class IntComparator extends WritableComparator {

     public IntComparator() {
         super(IntWritable.class);
     }

     @Override
     public int compare(byte[] b1, int s1, int l1,
             byte[] b2, int s2, int l2) {

         Integer v1 = ByteBuffer.wrap(b1, s1, l1).getInt();
         Integer v2 = ByteBuffer.wrap(b2, s2, l2).getInt();

         return v1.compareTo(v2) * (-1);
     }
 }
}

