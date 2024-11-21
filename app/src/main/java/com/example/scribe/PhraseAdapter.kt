package com.example.scribe

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.time.format.DateTimeFormatter

class PhraseAdapter(
    private var gData: LiveData<List<String>>,
//    private var dataSet: List<String>,
    // TODO give phraseadapter a way to access sharedviewmodel
    private val sharedViewModel: SharedViewModel // TODO mod

//    private val activityResult: OnActivityResult
) : RecyclerView.Adapter<PhraseAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val timeView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.text_bubble_output)
            timeView = view.findViewById(R.id.text_bubble_timestamp)
        }
    }

//    private val mDiffer = AsyncListDiffer(this, PhraseDiffCallback)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_bubble, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // TODO livedata position
//        val (body, time) = getPhrase(dataSet[position])
        val (body, time) = getPhrase(sharedViewModel.gDataIndex(position))
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = body
        viewHolder.timeView.text = time.format(formatter)
    }

    // unused
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = sharedViewModel.gDataSize()

    fun submitList(currentCSV: File, newData: List<String>) {
        sharedViewModel.gDataSet(newData)
    }
}