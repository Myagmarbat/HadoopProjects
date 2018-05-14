package hybrid_inmapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Pair, IntWritable> {
	  
	HashMap<Pair, Integer> h;

	@Override
	protected void setup(
			Mapper<LongWritable, Text, Pair, IntWritable>.Context context)
			throws IOException, InterruptedException {
		h = new HashMap<Pair, Integer>();
	}

	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, Pair, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString().trim();
		String[] input = line.split(" ");

		for (int i = 0; i < input.length; i++) {
			for (int j = i + 1; j < input.length && !input[i].equals(input[j]); j++) {
				Pair p = new Pair(input[i], input[j]);
				if (h.get(p) == null) {
					h.put(p, 1);
				} else {
					h.put(p, h.get(p) + 1);
				}
			}
		}
	}

	@Override
	protected void cleanup(
			Mapper<LongWritable, Text, Pair, IntWritable>.Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
		for (Entry<Pair, Integer> entry : h.entrySet()) {
			context.write(entry.getKey(), new IntWritable(entry.getValue()));
		}
	}
}
