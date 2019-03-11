package foo;

import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import hw1_dg.hash_count;
        
public class user_count {
        
 public static class Map1 extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    String date;    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line, "\t");
        
        while (tokenizer.hasMoreTokens()) {
        	
        	String token = tokenizer.nextToken();

	            if (token.matches("([0-9]){8}.*") && token.length()>= 10) {
	                //date = token;
	            	date = token.substring(0, 10); 
	            			//+ "-" + token.substring(5,7) + "-" + token.substring(9,10);
	            	word.set(date);
	                context.write(word, one);
	            }
            }
        	/*word.set(tokenizer.nextToken());
            context.write(word, one);*/
        }
    } 
 
 public static class Map2 extends Mapper<LongWritable, Text, Text, LongWritable> {
	    //private final static IntWritable one = new IntWritable(1);
	    private Text word = new Text();
	    //String uid_str;
	    //Text uid;
	    String city;    
	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	        String line = value.toString();
	        LongWritable uid = key;
	        StringTokenizer tokenizer = new StringTokenizer(line, "\t");
	    	//uid_str = line.substring(0,7);
	    	//uid.set(uid_str);
	    	
	        while (tokenizer.hasMoreTokens()) {
	        	
	        	String token = tokenizer.nextToken();

		            if (token.matches("[a-zA-Z]*[,].[a-zA-Z]*") && token.length()>= 10) {
		                city = token;
		            	city = token.substring(0, 10); 
		            			//+ "-" + token.substring(5,7) + "-" + token.substring(9,10);
		            	word.set(city);	            	
		                context.write(word, uid);
		            }
	            }
	        	/*word.set(tokenizer.nextToken());
	            context.write(word, one);*/
	        }
	    } 
 
        
 public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        context.write(key, new IntWritable(sum));
    }
 }
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
        Job job = new Job(conf, "date-count");
    
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    job.setJarByClass(hash_count.class);
    
    job.waitForCompletion(true);
 }
        
}