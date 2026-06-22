# ChamaTI Evolution — Design Document

## Context

Implementation supplement to the PRD dated 21 June 2026. The PRD specifies 5 functional requirements (RF01-RF05), of which RF01 (Drawer), RF02 (Hybrid Persistence), RF03 (Camera), RF04 (Stats), and RF05 (About) are already largely implemented. Three remaining gaps need implementation:

1. BaseDrawerActivity for uniform drawer navigation across all main screens
2. Image compression (70% quality, 1024px max) + ParseFile upload to Back4App
3. FLAG_ACTIVITY_REORDER_TO_FRONT for clean activity stack management

---

## 1. BaseDrawerActivity

### Architecture

Abstract base class that wraps any activity content in a `DrawerLayout` with `NavigationView` and `MaterialToolbar`. Activities inherit navigation behavior without duplicating XML.

### Layout

`res/layout/activity_base_drawer.xml`:
```
DrawerLayout
└── LinearLayout (vertical)
    ├── MaterialToolbar
    └── FrameLayout (content_frame)
NavigationView @end
```

### Base Class

`java/com/example/chamati/BaseDrawerActivity.java`:
- Extends `AppCompatActivity`
- `onCreate()`: sets content view to `activity_base_drawer`, configures toolbar, drawer toggle, navigation listener
- `setContentLayout(int layoutRes)`: inflates child layout into `content_frame`
- `setActivityTitle(String)`: updates toolbar title
- `setSelectedNavItem(int menuItemId)`: highlights current item in NavigationView
- Navigation listener dispatches intents with `FLAG_ACTIVITY_REORDER_TO_FRONT`

### Activities Migrated

| Activity | Change |
|---|---|
| `MainActivity` | Extends `BaseDrawerActivity`, content via `setContentLayout()` |
| `ListaChamadoActivity` | Extends `BaseDrawerActivity`, replaces own Toolbar with inherited |
| `EstatisticasActivity` | Extends `BaseDrawerActivity` |
| `SobreActivity` | Extends `BaseDrawerActivity` |

**Not migrated** (form screens, no drawer needed): `CadastroChamadoActivity`, `AtendimentoActivity`.

### Navigation Items

From `drawer_menu.xml`: Novo Chamado, Listagem, Estatisticas, Sobre.
Each intent uses `FLAG_ACTIVITY_REORDER_TO_FRONT`.

---

## 2. Image Compression + ParseFile Upload

### ImageUtils

`java/com/example/chamati/Utils/ImageUtils.java`:

```java
public static String compressImage(String imagePath, int maxDimension, int quality)
```

- Reads bitmap from path
- Calculates scale factor to fit within `maxDimension` (1024px) preserving aspect ratio
- Creates scaled bitmap via `Bitmap.createScaledBitmap()`
- Compresses to JPEG with `quality` (70) via `Bitmap.compress()`
- Overwrites original file
- Returns compressed file path

### Integration Points

1. **CadastroChamadoActivity**: In `dispatchTakePictureIntent()` or in `cameraLauncher` callback, call `ImageUtils.compressImage(currentPhotoPath, 1024, 70)` after photo is taken
2. **ChamadoCloudManager.salvarChamadoCloud()**: Create `ParseFile` from compressed image, upload before saving ParseObject

### Cloud Schema Change

Back4App `Chamado` class now stores image as `ParseFile` in field `imagem` (instead of plain text `imagemPath`).

---

## 3. Navigation Flags

All `startActivity()` calls from `NavigationView` item selection use:
```java
intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
```

This ensures:
- Tapping "Listagem" while already on ListaChamadoActivity doesn't create a duplicate
- Navigation sidebar doesn't build an infinite activity stack
- Existing activity instances are reused

---

## Files Modified/Created

### New Files
| File | Purpose |
|---|---|
| `BaseDrawerActivity.java` | Abstract drawer base class |
| `Utils/ImageUtils.java` | Image compression utility |
| `res/layout/activity_base_drawer.xml` | Base drawer layout |

### Modified Files
| File | Change |
|---|---|
| `MainActivity.java` | Extend BaseDrawerActivity |
| `ListaChamadoActivity.java` | Extend BaseDrawerActivity |
| `EstatisticasActivity.java` | Extend BaseDrawerActivity |
| `SobreActivity.java` | Extend BaseDrawerActivity |
| `CadastroChamadoActivity.java` | Add image compression call |
| `ChamadoCloudManager.java` | ParseFile upload for images |
