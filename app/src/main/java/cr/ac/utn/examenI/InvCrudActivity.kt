package cr.ac.utn.examenI

import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.InvViewModel
import java.time.LocalDateTime
import cr.ac.utn.examenI.domain.model.Person
import cr.ac.utn.examenI.domain.model.InvInventory

class InvCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtProductCode: EditText
    private lateinit var txtProductName: EditText
    private lateinit var txtQuantity: EditText
    private lateinit var spnSupplier: Spinner
    private lateinit var txtUnitCost: EditText
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null
    private val suppliers = arrayOf("Proveedor A", "Proveedor B", "Proveedor C")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inv_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId = findViewById(R.id.txtId)
        txtName = findViewById(R.id.txtName)
        txtFirstName = findViewById(R.id.txtFirstName)
        txtLastName = findViewById(R.id.txtLastName)
        txtPhone = findViewById(R.id.txtPhone)
        txtEmail = findViewById(R.id.txtEmail)
        txtAddress = findViewById(R.id.txtAddress)
        txtProductCode = findViewById(R.id.txtProductCode)
        txtProductName = findViewById(R.id.txtProductName)
        txtQuantity = findViewById(R.id.txtQuantity)
        spnSupplier = findViewById(R.id.spnSupplier)
        txtUnitCost = findViewById(R.id.txtUnitCost)
        txtDateTime = findViewById(R.id.txtDateTime)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, suppliers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSupplier.adapter = adapter

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtDateTime.setText(util.formatDateTime(LocalDateTime.now()))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { doSave() }

        val btnDelete = findViewById<Button>(R.id.btnDelete)
        btnDelete.visibility = if (currentId.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        btnDelete.setOnClickListener { doDelete() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingData(id: String) {
        txtId.setText(txtProductCode.text.toString())
        txtProductCode.setText(id)
        val item = InvViewModel.getById(id) ?: return
        txtId.setText(item.person.id)
        txtId.isEnabled = false
        txtName.setText(item.person.name)
        txtFirstName.setText(item.person.firstName)
        txtLastName.setText(item.person.lastName)
        txtPhone.setText(item.person.phone.toString())
        txtEmail.setText(item.person.email)
        txtAddress.setText(item.person.address)
        txtProductCode.setText(item.productCode)
        txtProductCode.isEnabled = false
        txtProductName.setText(item.productName)
        txtQuantity.setText(item.quantity.toString())
        val pos = suppliers.indexOf(item.supplier)
        if (pos >= 0) spnSupplier.setSelection(pos)
        txtUnitCost.setText(item.unitCost.toString())
        txtDateTime.setText(util.formatDateTime(item.entryDateTime))
    }

    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val prodCode = txtProductCode.text.toString().trim()
        val prodName = txtProductName.text.toString().trim()
        val qty = txtQuantity.text.toString().toIntOrNull() ?: 0
        val dateTimeStr = txtDateTime.text.toString().trim()

        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phone = txtPhone.text.toString().trim().toIntOrNull() ?: 0
        val email = txtEmail.text.toString().trim()
        val address = txtAddress.text.toString().trim()
        val unitCost = txtUnitCost.text.toString().toDoubleOrNull()

        if (id.isEmpty() || name.isEmpty() || firstName.isEmpty() ||
            lastName.isEmpty() || email.isEmpty() || address.isEmpty() ||
            prodCode.isEmpty() || prodName.isEmpty() ||
            txtPhone.text.toString().trim().isEmpty() ||
            txtQuantity.text.toString().trim().isEmpty() ||
            txtUnitCost.text.toString().trim().isEmpty() ||
            dateTimeStr.isEmpty()) {

            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.error = getString(R.string.TextSaveError)
            return
        }

        if (phone <= 0) {
            txtPhone.error = getString(R.string.TextSaveError)
            return
        }

        if (qty <= 0) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        if (unitCost == null || unitCost <= 0 || !unitCost.isFinite()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val dt = util.parseStringToDateTimeModern(dateTimeStr)
        if (dt == null || dt.isAfter(LocalDateTime.now())) {
            txtDateTime.error = getString(R.string.TextSaveError)
            return
        }

        val person = Person(
            id = id,
            name = name,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            address = address
        )

        val item = InvInventory(
            id = prodCode,
            person = person,
            productName = prodName,
            quantity = qty,
            supplier = spnSupplier.selectedItem.toString(),
            unitCost = unitCost,
            entryDateTime = dt
        )

        if (currentId.isNullOrEmpty()) {
            if (InvViewModel.getById(prodCode) != null) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }
            InvViewModel.save(item)
            Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            util.showDialogCondition(this, getString(R.string.TextSaveQuestion)) {
                if (InvViewModel.update(item)) {
                    Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun doDelete() {
        val id = txtProductCode.text.toString().trim()
        if (id.isEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            if (InvViewModel.delete(id)) {
                Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.TextDeleteError), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        menu?.findItem(R.id.mnu_delete)?.isVisible = !currentId.isNullOrEmpty()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_save -> { doSave(); true }
            R.id.mnu_delete -> { doDelete(); true }
            R.id.mnu_cancel -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
