# ChamaTI Evolution Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement remaining PRD gaps — BaseDrawerActivity, image compression/ParseFile, navigation flags.

**Architecture:** Abstract `BaseDrawerActivity` wraps content in DrawerLayout; `ImageUtils` compresses photos; `ChamadoCloudManager` uploads ParseFile.

**Tech Stack:** Java 17, Android SDK 34, Material Design 3, Parse SDK 1.17.3, SQLite

---

### Task 1: Create activity_base_drawer.xml

**Files:**
- Create: `app/src/main/res/layout/activity_base_drawer.xml`
- Reference: `app/src/main/res/layout/nav_header_main.xml`
- Reference: `app/src/main/res/menu/drawer_menu.xml`

- [ ] **Step 1: Create the base layout**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="@color/background_dark"
            app:titleTextColor="@color/text_primary" />

        <FrameLayout
            android:id="@+id/content_frame"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </LinearLayout>

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@color/card_background"
        app:headerLayout="@layout/nav_header_main"
        app:itemIconTint="@color/drawer_item_color"
        app:itemTextColor="@color/drawer_item_color"
        app:menu="@menu/drawer_menu" />

</androidx.drawerlayout.widget.DrawerLayout>
```

---

### Task 2: Create BaseDrawerActivity.java

**Files:**
- Create: `app/src/main/java/com/example/chamati/BaseDrawerActivity.java`

- [ ] **Step 1: Create the abstract base class**

```java
package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.FrameLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected MaterialToolbar toolbar;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_novo_chamado) {
                intent = new Intent(this, CadastroChamadoActivity.class);
            } else if (itemId == R.id.nav_listagem) {
                intent = new Intent(this, ListaChamadoActivity.class);
            } else if (itemId == R.id.nav_estatisticas) {
                intent = new Intent(this, EstatisticasActivity.class);
            } else if (itemId == R.id.nav_sobre) {
                intent = new Intent(this, SobreActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout container = findViewById(R.id.content_frame);
        if (container != null) {
            container.removeAllViews();
            LayoutInflater.from(this).inflate(layoutResID, container);
        }
    }

    protected void setActivityTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    protected void setSelectedNavItem(int itemId) {
        navigationView.setCheckedItem(itemId);
    }
}
```

- [ ] **Step 2: Add drawer string resources**

Add to `app/src/main/res/values/strings.xml`:
```xml
<string name="navigation_drawer_open">Abrir menu</string>
<string name="navigation_drawer_close">Fechar menu</string>
```

---

### Task 3: Extract MainActivity content layout

**Files:**
- Delete then Create: `app/src/main/res/layout/activity_main.xml`

**Note:** Remove the DrawerLayout/Toolbar/NavigationView wrapper. Keep only the inner content LinearLayout with logo, stats cards, and action buttons. The DrawerLayout and Toolbar come from `activity_base_drawer.xml`.

- [ ] **Step 1: Rewrite activity_main.xml to contain only the content body**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_dark"
    android:orientation="vertical"
    android:padding="20dp">

    <!-- Logo Section -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="40dp"
        android:layout_marginBottom="40dp"
        android:gravity="center"
        android:orientation="horizontal">

        <ImageView
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@drawable/ic_fire" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:text="Chama"
            android:textColor="@color/white"
            android:textSize="32sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="TI"
            android:textColor="@color/chama_cyan"
            android:textSize="32sp"
            android:textStyle="bold" />
    </LinearLayout>

    <!-- Stats Section -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:weightSum="3">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="12dp">

                <TextView
                    android:id="@+id/tvCountAbertos"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0"
                    android:textColor="@color/status_aberto"
                    android:textSize="20sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Abertos"
                    android:textColor="@color/text_secondary"
                    android:textSize="12sp" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="12dp">

                <TextView
                    android:id="@+id/tvCountAndamento"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0"
                    android:textColor="@color/status_andamento"
                    android:textSize="20sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Andamento"
                    android:textColor="@color/text_secondary"
                    android:textSize="12sp" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="12dp">

                <TextView
                    android:id="@+id/tvCountConcluidos"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0"
                    android:textColor="@color/status_concluido"
                    android:textSize="20sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Concluídos"
                    android:textColor="@color/text_secondary"
                    android:textSize="12sp" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>
    </LinearLayout>

    <View
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <!-- Action Buttons -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/btnNovoChamado"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        app:cardBackgroundColor="@color/chama_red"
        app:cardCornerRadius="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal"
            android:padding="24dp">

            <ImageView
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:src="@android:drawable/ic_input_add"
                app:tint="@color/white" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginStart="16dp"
                android:text="Novo Chamado"
                android:textColor="@color/white"
                android:textSize="18sp"
                android:textStyle="bold" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/btnVerChamados"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="40dp"
        app:cardBackgroundColor="@color/chama_cyan"
        app:cardCornerRadius="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal"
            android:padding="24dp">

            <ImageView
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:src="@android:drawable/ic_menu_sort_by_size"
                app:tint="@color/background_dark" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginStart="16dp"
                android:text="Ver Chamados"
                android:textColor="@color/background_dark"
                android:textSize="18sp"
                android:textStyle="bold" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

</LinearLayout>
```

---

### Task 4: Refactor MainActivity.java

**Files:**
- Modify: `app/src/main/java/com/example/chamati/MainActivity.java`

**Changes:**
- Extend `BaseDrawerActivity` instead of `AppCompatActivity`
- Remove all DrawerLayout, Toolbar, NavigationView setup (now in base)
- Call `setContentView(R.layout.activity_main)` is inherited from base
- Set title and selected nav item

- [ ] **Step 1: Rewrite MainActivity.java**

```java
package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.chamati.DataBase.DataBaseHelper;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends BaseDrawerActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos;
    private MaterialCardView btnNovoChamado, btnVerChamados;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityTitle("ChamaTI");
        setSelectedNavItem(R.id.nav_novo_chamado);

        dbHelper = new DataBaseHelper(this);

        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);
        btnNovoChamado = findViewById(R.id.btnNovoChamado);
        btnVerChamados = findViewById(R.id.btnVerChamados);

        btnNovoChamado.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroChamadoActivity.class);
            startActivity(intent);
        });

        btnVerChamados.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaChamadoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        int abertos = dbHelper.getCountByStatus("aberto");
        int andamento = dbHelper.getCountByStatus("andamento");
        int concluidos = dbHelper.getCountByStatus("fechado");

        tvCountAbertos.setText(String.valueOf(abertos));
        tvCountAndamento.setText(String.valueOf(andamento));
        tvCountConcluidos.setText(String.valueOf(concluidos));
    }
}
```

---

### Task 5: Refactor ListaChamadoActivity layout

**Files:**
- Delete then Create: `app/src/main/res/layout/activity_lista_chamado.xml`
- Create: `app/src/main/res/menu/menu_lista_chamado.xml`

**Changes:**
- Remove CoordinatorLayout wrapper (replace with LinearLayout root that goes inside content_frame)
- Remove Toolbar (comes from base)
- Convert filter ImageView from toolbar child to standalone menu item via XML

- [ ] **Step 1: Create filter menu XML**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_filter"
        android:icon="@android:drawable/ic_menu_search"
        android:title="Filtrar"
        app:showAsAction="always" />
</menu>
```

- [ ] **Step 2: Rewrite activity_lista_chamado.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_dark">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvContadorChamados"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="20dp"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"
            android:text="0 chamados"
            android:textColor="@color/text_secondary"
            android:textSize="14sp" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rvChamados"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clipToPadding="false"
            android:padding="20dp" />
    </LinearLayout>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAdd"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="24dp"
        android:src="@android:drawable/ic_input_add"
        app:backgroundTint="@color/chama_red"
        app:tint="@color/white" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

---

### Task 6: Refactor ListaChamadoActivity.java

**Files:**
- Modify: `app/src/main/java/com/example/chamati/ListaChamadoActivity.java`

**Changes:**
- Extend `BaseDrawerActivity`
- Remove Toolbar setup (comes from base)
- Add `onCreateOptionsMenu` and `onOptionsItemSelected` for filter menu

- [ ] **Step 1: Rewrite ListaChamadoActivity.java**

```java
package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ListaChamadoActivity extends BaseDrawerActivity implements FiltrosBottomSheet.OnFiltroListener {

    private RecyclerView rvChamados;
    private ChamadoAdapter adapter;
    private DataBaseHelper dbHelper;
    private TextView tvContador;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamado);
        setActivityTitle("Meus Chamados");
        setSelectedNavItem(R.id.nav_listagem);

        dbHelper = new DataBaseHelper(this);

        rvChamados = findViewById(R.id.rvChamados);
        tvContador = findViewById(R.id.tvContadorChamados);
        fabAdd = findViewById(R.id.fabAdd);

        rvChamados.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroChamadoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_chamado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            FiltrosBottomSheet bottomSheet = new FiltrosBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "filtros");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarChamados();
    }

    private void carregarChamados() {
        List<Chamado> lista = dbHelper.getAllChamados();
        tvContador.setText(lista.size() + " chamados");

        if (adapter == null) {
            adapter = new ChamadoAdapter(lista, this);
            rvChamados.setAdapter(adapter);
        } else {
            adapter.updateList(lista);
        }
    }

    @Override
    public void onFiltroAplicado(Integer tipo, String status, String dataIni, String dataFim) {
        List<Chamado> filtrados = dbHelper.getChamadosFiltrados(tipo, status, dataIni, dataFim);
        adapter.updateList(filtrados);
        tvContador.setText(filtrados.size() + " chamados");
    }

    @Override
    public void onFiltroLimpo() {
        carregarChamados();
    }
}
```

---

### Task 7: Refactor EstatisticasActivity layout

**Files:**
- Delete then Create: `app/src/main/res/layout/activity_estatisticas.xml`

**Changes:** Remove Toolbar (comes from base), keep only stats content.

- [ ] **Step 1: Rewrite activity_estatisticas.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_dark"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="40dp">

    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="24dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="vertical"
            android:padding="32dp">

            <TextView
                android:id="@+id/tvTotal"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="0"
                android:textColor="@color/chama_cyan"
                android:textSize="48sp"
                android:textStyle="bold" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="Total de Chamados"
                android:textColor="@color/text_secondary"
                android:textSize="16sp" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:weightSum="3">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:id="@+id/tvCountAbertos"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0"
                    android:textColor="@color/status_aberto"
                    android:textSize="24sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:text="Abertos"
                    android:textColor="@color/text_secondary"
                    android:textSize="12sp" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:id="@+id/tvCountAndamento"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0"
                    android:textColor="@color/status_andamento"
                    android:textSize="24sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:text="Andamento"
                    android:textColor="@color/text_secondary"
                    android:textSize="12sp" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:id="@+id/tvCountConcluidos"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0"
                    android:textColor="@color/status_concluido"
                    android:textSize="24sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:text="Concluídos"
                    android:textColor="@color/text_secondary"
                    android:textSize="12sp" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>
    </LinearLayout>
</LinearLayout>
```

---

### Task 8: Refactor EstatisticasActivity.java

**Files:**
- Modify: `app/src/main/java/com/example/chamati/EstatisticasActivity.java`

**Changes:**
- Extend `BaseDrawerActivity`
- Remove Toolbar setup (comes from base)

- [ ] **Step 1: Rewrite EstatisticasActivity.java**

```java
package com.example.chamati;

import android.os.Bundle;
import android.widget.TextView;
import com.example.chamati.DataBase.DataBaseHelper;

public class EstatisticasActivity extends BaseDrawerActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos, tvTotal;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);
        setActivityTitle("Estatísticas");
        setSelectedNavItem(R.id.nav_estatisticas);

        dbHelper = new DataBaseHelper(this);

        tvTotal = findViewById(R.id.tvTotal);
        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        int total = dbHelper.getCountByStatus("aberto")
                + dbHelper.getCountByStatus("andamento")
                + dbHelper.getCountByStatus("fechado");

        tvTotal.setText(String.valueOf(total));
        tvCountAbertos.setText(String.valueOf(dbHelper.getCountByStatus("aberto")));
        tvCountAndamento.setText(String.valueOf(dbHelper.getCountByStatus("andamento")));
        tvCountConcluidos.setText(String.valueOf(dbHelper.getCountByStatus("fechado")));
    }
}
```

---

### Task 9: Refactor SobreActivity layout

**Files:**
- Delete then Create: `app/src/main/res/layout/activity_sobre.xml`

**Changes:** Remove Toolbar (comes from base), keep only about content.

- [ ] **Step 1: Rewrite activity_sobre.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_dark"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="40dp">

    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cardCornerRadius="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="vertical"
            android:padding="32dp">

            <ImageView
                android:layout_width="64dp"
                android:layout_height="64dp"
                android:src="@drawable/ic_fire" />

            <LinearLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:orientation="horizontal">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Chama"
                    android:textColor="@color/white"
                    android:textSize="28sp"
                    android:textStyle="bold" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="TI"
                    android:textColor="@color/chama_cyan"
                    android:textSize="28sp"
                    android:textStyle="bold" />
            </LinearLayout>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:text="Sistema de Gerenciamento de Chamados"
                android:textColor="@color/text_secondary"
                android:textSize="14sp"
                android:gravity="center" />

            <View
                android:layout_width="80dp"
                android:layout_height="1dp"
                android:layout_marginTop="24dp"
                android:layout_marginBottom="24dp"
                android:background="@color/text_secondary" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Versão 2.0"
                android:textColor="@color/text_secondary"
                android:textSize="12sp" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:cardCornerRadius="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="24dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/integrantes_label"
                android:textColor="@color/text_secondary"
                android:textSize="12sp" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp"
                android:text="@string/integrantes_nomes"
                android:textColor="@color/text_primary"
                android:textSize="16sp"
                android:textStyle="bold" />

            <View
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:layout_marginTop="16dp"
                android:layout_marginBottom="16dp"
                android:background="@color/text_secondary" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/turma_label"
                android:textColor="@color/text_secondary"
                android:textSize="12sp" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp"
                android:text="@string/turma_valor"
                android:textColor="@color/text_primary"
                android:textSize="16sp"
                android:textStyle="bold" />

        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>
</LinearLayout>
```

---

### Task 10: Refactor SobreActivity.java

**Files:**
- Modify: `app/src/main/java/com/example/chamati/SobreActivity.java`

**Changes:**
- Extend `BaseDrawerActivity`
- Remove Toolbar setup (comes from base)

- [ ] **Step 1: Rewrite SobreActivity.java**

```java
package com.example.chamati;

import android.os.Bundle;

public class SobreActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        setActivityTitle("Sobre o Sistema");
        setSelectedNavItem(R.id.nav_sobre);
    }
}
```

---

### Task 11: Create ImageUtils.java

**Files:**
- Create: `app/src/main/java/com/example/chamati/Utils/ImageUtils.java`

- [ ] **Step 1: Create the Utils directory**

```bash
mkdir -p app/src/main/java/com/example/chamati/Utils
```

- [ ] **Step 2: Create ImageUtils.java**

```java
package com.example.chamati.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static String compressImage(String imagePath, int maxDimension, int quality) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int width = options.outWidth;
        int height = options.outHeight;

        int scale = 1;
        while (width / scale > maxDimension || height / scale > maxDimension) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        Bitmap original = BitmapFactory.decodeFile(imagePath, options);

        if (original == null) return imagePath;

        float aspectRatio = (float) original.getWidth() / (float) original.getHeight();
        int newWidth, newHeight;
        if (original.getWidth() > original.getHeight()) {
            newWidth = Math.min(original.getWidth(), maxDimension);
            newHeight = Math.round(newWidth / aspectRatio);
        } else {
            newHeight = Math.min(original.getHeight(), maxDimension);
            newWidth = Math.round(newHeight * aspectRatio);
        }

        Bitmap scaled = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);

        File file = new File(imagePath);
        try (FileOutputStream out = new FileOutputStream(file)) {
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (IOException e) {
            e.printStackTrace();
            return imagePath;
        } finally {
            if (original != scaled) {
                original.recycle();
            }
            scaled.recycle();
        }

        return imagePath;
    }
}
```

---

### Task 12: Update CadastroChamadoActivity to compress photo

**Files:**
- Modify: `app/src/main/java/com/example/chamati/CadastroChamadoActivity.java`

**Changes:** Add image compression call after photo is taken.

- [ ] **Step 1: Add import and compression call**

Add import:
```java
import com.example.chamati.Utils.ImageUtils;
```

Modify the `cameraLauncher` callback to compress the image after capture:

Old code (lines 43-49):
```java
private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicture(),
        success -> {
            if (success) {
                ivFotoPreview.setVisibility(ImageView.VISIBLE);
                ivFotoPreview.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
            }
        });
```

New code:
```java
private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicture(),
        success -> {
            if (success) {
                ImageUtils.compressImage(currentPhotoPath, 1024, 70);
                ivFotoPreview.setVisibility(ImageView.VISIBLE);
                ivFotoPreview.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
            }
        });
```

---

### Task 13: Update ChamadoCloudManager to upload image as ParseFile

**Files:**
- Modify: `app/src/main/java/com/example/chamati/Cloud/ChamadoCloudManager.java`

**Changes:** Upload compressed image as ParseFile before saving ParseObject. Store ParseFile URL reference.

- [ ] **Step 1: Add ParseFile import and modify salvarChamadoCloud**

Old code (lines 16-41):
```java
public void salvarChamadoCloud(Chamado chamado, SyncCallback callback) {
    ParseObject parseObject = new ParseObject("Chamado");
    parseObject.put("titulo", chamado.getTitulo());
    parseObject.put("descricao", chamado.getDescricao());
    parseObject.put("local", chamado.getLocal());
    parseObject.put("tipo", chamado.getTipo());
    parseObject.put("dataCadastro", chamado.getDataCadastro());
    parseObject.put("status", chamado.getStatus());
    parseObject.put("solucao", chamado.getSolucao());

    String imagemPath = chamado.getImagemPath();
    if (imagemPath != null && !imagemPath.isEmpty()) {
        parseObject.put("imagemPath", imagemPath);
    }

    parseObject.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null) {
                callback.onSuccess(parseObject.getObjectId());
            } else {
                callback.onError(e.getMessage());
            }
        }
    });
}
```

New code:
```java
public void salvarChamadoCloud(Chamado chamado, SyncCallback callback) {
    ParseObject parseObject = new ParseObject("Chamado");
    parseObject.put("titulo", chamado.getTitulo());
    parseObject.put("descricao", chamado.getDescricao());
    parseObject.put("local", chamado.getLocal());
    parseObject.put("tipo", chamado.getTipo());
    parseObject.put("dataCadastro", chamado.getDataCadastro());
    parseObject.put("status", chamado.getStatus());
    parseObject.put("solucao", chamado.getSolucao());

    String imagemPath = chamado.getImagemPath();

    if (imagemPath != null && !imagemPath.isEmpty()) {
        File imageFile = new File(imagemPath);
        if (imageFile.exists()) {
            ParseFile parseFile = new ParseFile(imageFile);
            parseFile.saveInBackground(e -> {
                if (e == null) {
                    parseObject.put("imagem", parseFile);
                    parseObject.put("imagemPath", parseFile.getUrl());
                }
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException saveError) {
                        if (saveError == null) {
                            callback.onSuccess(parseObject.getObjectId());
                        } else {
                            callback.onError(saveError.getMessage());
                        }
                    }
                });
            });
            return;
        }
    }

    parseObject.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null) {
                callback.onSuccess(parseObject.getObjectId());
            } else {
                callback.onError(e.getMessage());
            }
        }
    });
}
```

Add the import:
```java
import com.parse.ParseFile;
import java.io.File;
```

---

### Task 14: Verify build compiles

**Files:**
- N/A — run build check

- [ ] **Step 1: Clean and build**

```bash
./gradlew clean assembleDebug
```

Expected: BUILD SUCCESSFUL

If any compile errors, fix them inline. Likely issues:
- Missing imports in modified files
- Layout ID mismatches (e.g., R.id.btnFiltro removed from activity_lista_chamado.xml — already handled by menu approach)
