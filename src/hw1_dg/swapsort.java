package hw1_dg;

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

import hw1_dg.wordcount.Map;
import hw1_dg.wordcount.Reduce;

//from apache's git

//mapper

/** A {@link Mapper} that swaps keys and values. */
@InterfaceAudience.Public
@InterfaceStability.Stable
public class swapsort 
  extends Mapper<Text , IntWritable , IntWritable  , Text>{
 	  
	  /** The inverse function.  Input keys and values are swapped.*/
	  @Override
	  public void map(Text key, IntWritable value, Context context) 
			  throws IOException, InterruptedException {
	  
		  context.write(value, key);
	  
	  }


}
/*
public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
    Job job = new Job(conf, "KVSwitchSort");
    
    job.setOutputKeyClass(LongWritable.class);
    job.setOutputValueClass(Text.class);
    
    job.setOutputKeyClass(LongWritable.class);
    job.setOutputValueClass(Text.class);
        
    job.setMapOutputValueClass(LongWritable.class);
    job.setMapOutputKeyClass(Text.class);
    
    job.setMapperClass(InverseMapper.class);
    job.setReducerClass(Reduce.class);
 
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    job.setJarByClass(InverseMapper.class);
    
    job.waitForCompletion(true);
    
    int exitCode = ToolRunner.run(new Driver(), args);
    System.exit(exitCode);
  }
*/


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
     
     Job job = new Job(conf, "wordcount");
 
 job.setOutputKeyClass(IntWritable.class);
 job.setOutputValueClass(Text.class);
     
 job.setMapperClass(Map.class);
 job.setReducerClass(Reduce.class);
     
 job.setInputFormatClass(TextInputFormat.class);
 job.setOutputFormatClass(TextOutputFormat.class);
     
 FileInputFormat.addInputPath(job, new Path(args[0]));
 FileOutputFormat.setOutputPath(job, new Path(args[1]));
 
 job.setJarByClass(wordcount.class);
 
 job.waitForCompletion(true);
}
     
}