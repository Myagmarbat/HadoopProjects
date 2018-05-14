package hybrid_inmapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Reduce extends Reducer<Pair, IntWritable, Text, CustomMap> {

	private HashMap<String, Integer> sumMap;

	private String currentItem;

	private double total;

	@Override
	protected void setup(
			Reducer<Pair, IntWritable, Text, CustomMap>.Context context)
			throws IOException, InterruptedException {
		sumMap = new HashMap<String, Integer>();
		total = 0.0;
	}

	@Override
	protected void reduce(Pair pair, Iterable<IntWritable> values,
			Reducer<Pair, IntWritable, Text, CustomMap>.Context context)
			throws IOException, InterruptedException {

		if (currentItem == null) {
			currentItem = pair.getKey1().toString();
		} else if (!currentItem.equals(pair.getKey1().toString())) {
			CustomMap resultMap = new CustomMap();
			for (Entry<String, Integer> entry : sumMap.entrySet()) {
				double r = entry.getValue() / total;
				DecimalFormat df = new DecimalFormat("#.###");
				resultMap.put(new Text(entry.getKey()), new DoubleWritable(
						Double.valueOf(df.format(r))));
			}
			context.write(new Text(currentItem), resultMap);
			currentItem = pair.getKey1().toString();
			sumMap = new HashMap<String, Integer>();
			total = 0;
		}

		int currentTotal = 0;
		for (IntWritable intWritable : values) {
			currentTotal += intWritable.get();
		}
		total += currentTotal;
		sumMap.put(pair.getKey2().toString(), currentTotal);
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		CustomMap resultMap = new CustomMap();
		for (Entry<String, Integer> entry : sumMap.entrySet()) {
			double r = entry.getValue() / total;
			DecimalFormat df = new DecimalFormat("#.###");
			resultMap.put(new Text(entry.getKey()),
					new DoubleWritable(Double.valueOf(df.format(r))));
		}
		context.write(new Text(currentItem), resultMap);
	}
}
