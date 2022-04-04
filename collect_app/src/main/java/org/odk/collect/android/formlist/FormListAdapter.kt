package org.odk.collect.android.formlist

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.odk.collect.android.R
import org.odk.collect.android.databinding.FormListItemBinding
import org.odk.collect.androidshared.ui.multiclicksafe.MultiClickGuard
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class FormListAdapter : RecyclerView.Adapter<FormListAdapter.ViewHolder>() {
    private var formItems = emptyList<FormListItem>()
    lateinit var listener: OnFormItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FormListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(formItems[position]) {
                binding.formTitle.text = this.formName

                binding.formSubtitle.text = binding.root.context.getString(R.string.version_number, this.formVersion)
                binding.formSubtitle.visibility = if (this.formVersion.isNotBlank()) View.VISIBLE else View.GONE

                binding.formSubtitle2.text = try {
                    SimpleDateFormat(binding.root.context.getString(R.string.added_on_date_at_time), Locale.getDefault()).format(this.dateOfCreation)
                } catch (e: IllegalArgumentException) {
                    Timber.e(e)
                    ""
                }

                binding.mapButton.visibility = if (this.geometryPath.isNotBlank()) View.VISIBLE else View.GONE

                binding.root.setOnClickListener {
                    if (MultiClickGuard.allowClick(javaClass.name)) {
                        listener.onFormClick(this.contentUri)
                    }
                }

                binding.mapButton.setOnClickListener {
                    if (MultiClickGuard.allowClick(javaClass.name)) {
                        listener.onMapButtonClick(this.databaseId)
                    }
                }
            }
        }
    }

    override fun getItemCount() = formItems.size

    inner class ViewHolder(val binding: FormListItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(formItems: List<FormListItem>) {
        this.formItems = formItems.toList()
        notifyDataSetChanged()
    }
}

interface OnFormItemClickListener {
    fun onFormClick(formUri: Uri)

    fun onMapButtonClick(id: Long)
}
