package org.odk.collect.entities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.odk.collect.entities.databinding.ListLayoutBinding

class EntitiesFragment(private val viewModelFactory: ViewModelProvider.Factory) : Fragment() {

    private val entitiesViewModel by viewModels<EntitiesViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ListLayoutBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ListLayoutBinding.bind(view)
        binding.list.layoutManager = LinearLayoutManager(requireContext())

        val dataset = EntitiesFragmentArgs.fromBundle(requireArguments()).dataset
        entitiesViewModel.getEntities(dataset).observe(viewLifecycleOwner) {
            binding.list.adapter = EntitiesAdapter(it)
        }
    }
}

private class EntitiesAdapter(private val data: List<Entity>) :
    RecyclerView.Adapter<EntityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): EntityViewHolder {
        return EntityViewHolder(parent.context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: EntityViewHolder, position: Int) {
        val entity = data[position]
        viewHolder.setEntity(entity)
    }
}

private class EntityViewHolder(context: Context) : ViewHolder(EntityItemView(context)) {

    fun setEntity(entity: Entity) {
        (itemView as EntityItemView).setEntity(entity)
    }
}
