package stripes_inmapper;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;

public class CustomMap extends MapWritable {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append("[");
		for (Entry<Writable, Writable> entry : this.entrySet()) {
			if(!first){
				sb.append(",");
			}
			sb.append(String.format("(%s, %s)", entry.getKey().toString(), entry
					.getValue().toString()));
			if(first){
				first = !first;
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
