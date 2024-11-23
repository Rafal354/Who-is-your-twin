import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whosyourtwin.NamePercentage
import com.example.whosyourtwin.R

class NamePercentageAdapter(private val items: List<NamePercentage>) :
    RecyclerView.Adapter<NamePercentageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.textName)
        val percentageTextView: TextView = view.findViewById(R.id.textPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.percentageTextView.text = "${item.percentage}%"
    }

    override fun getItemCount(): Int = items.size
}
