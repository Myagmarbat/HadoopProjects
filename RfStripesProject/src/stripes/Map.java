package stripes;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Text, MapWritable> {

	MapWritable h;

	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, Text, MapWritable>.Context context)
			throws IOException, InterruptedException {
		
		
		String line = value.toString().trim();
		String[] inputs = line.split(" ");
		for (int i = 0; i < inputs.length; i++) {
			h = new MapWritable();			
			for (int j = i + 1; j < inputs.length && !inputs[i].equals(inputs[j]); j++) {
				if(h.get(new Text(inputs[j])) == null){
					h.put(new Text(inputs[j]), new IntWritable(1));
				} else {
					IntWritable inWritable = (IntWritable) h.get(new Text(inputs[j]));
					h.put(new Text(inputs[j]), new IntWritable(inWritable.get() + 1));
				}
			}
			context.write(new Text(inputs[i]), h);
		}
	}
}
