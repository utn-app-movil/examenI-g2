package cr.ac.utn.examenI

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cr.ac.utn.examenI.domain.model.Identifier
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.VacViewModel

class VacListActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var viewModel: VacViewModel
    private lateinit var rvList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vac_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vac_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[VacViewModel::class.java]

        rvList = findViewById(R.id.vac_rv_list)
        rvList.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.vac_btn_add).setOnClickListener {
            util.openActivity(this, VacCrudActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        rvList.adapter = ListAdapter(viewModel.getAll(), this)
    }

    override fun onItemClick(obj: Identifier) {
        util.openActivity(this, VacCrudActivity::class.java, EXTRA_ID, obj.id)
    }
}
