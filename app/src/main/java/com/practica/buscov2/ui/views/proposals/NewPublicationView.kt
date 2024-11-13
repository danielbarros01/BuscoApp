package com.practica.buscov2.ui.views.proposals

import com.practica.buscov2.ui.views.images.SelectImageFromGallery
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.R
import com.practica.buscov2.data.dataStore.StoreUbication
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.AlertSelectPicture
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.ButtonUbication
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.CommonFieldArea
import com.practica.buscov2.ui.components.InsertAsyncImage
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.SearchProfession
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.NewPublicationViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.professions.ProfessionsViewModel
import com.practica.buscov2.ui.viewModel.ubication.MapViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.images.UseCamera
import com.practica.buscov2.ui.views.maps.MapViewUI
import com.practica.buscov2.util.AppUtils.Companion.formatNumber
import kotlinx.coroutines.launch

@Composable
fun NewPublicationView(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProfession: ProfessionsViewModel,
    vmNewPublication: NewPublicationViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) {}
        }
    }


    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            NewPublication(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                token,
                vmProfession,
                vmNewPublication,
                vmLoading,
                mapVM,
                searchMapVM,
                navController
            )
        }
    }
}

@Composable
fun NewPublication(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    token: LoginToken?,
    vmProfession: ProfessionsViewModel,
    vmNewPublication: NewPublicationViewModel,
    vmLoading: LoadingViewModel,
    mapVM: MapViewModel,
    searchMapVM: SearchMapViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val isLoading by vmLoading.isLoading
    val error = vmNewPublication.error
    val showError = remember { mutableStateOf(false) }

    val openAlertSelectImage = remember { mutableStateOf(false) }
    val openCamera = remember { mutableStateOf(false) }
    val openGallery = remember { mutableStateOf(false) }

    val newUriPicture = remember {
        mutableStateOf(Uri.EMPTY)
    }

    val buttonEnabled by vmNewPublication.buttonEnabled

    val scope = rememberCoroutineScope()
    val ubicationDataStore = StoreUbication(context)
    val ubication = ubicationDataStore.getUbicationFlow().collectAsState(initial = null)
    val ubicationPublication = vmNewPublication.ubication
    val address = mapVM.address.value
    val coordinates = searchMapVM.placeCoordinates.value
    var setUbicationStart by remember { mutableStateOf(false) }

    fun updateLocation(location: LatLng) {
        searchMapVM.setLocation(location.latitude, location.longitude)
        searchMapVM.getLocation(location.latitude, location.longitude) { address ->
            address?.let {
                mapVM.setAddress(it.formatted_address)
            }
        }
        vmNewPublication.setUbication(location.latitude, location.longitude)
    }

    LaunchedEffect(Unit) {
        val location = ubication.value ?: LatLng(user.latitude!!, user.longitude!!)
        updateLocation(location)
        setUbicationStart = true
    }

    LaunchedEffect(ubicationPublication.value) {
        val location = ubicationPublication.value ?: LatLng(user.latitude!!, user.longitude!!)
        updateLocation(location)
    }


    //Mostrar error
    AlertError(showDialog = showError, error.value.title, error.value.message)
    //Mostrar loader
    if (isLoading) {
        LoaderMaxSize()
    }
    //Seleccionar forma de elegir imagen
    AlertSelectPicture(
        showDialog = openAlertSelectImage,
        openCamera = {
            openCamera.value = true
            openAlertSelectImage.value = false
        },
        openGallery = {
            openGallery.value = true
            openAlertSelectImage.value = false
        }
    )

    if (openCamera.value) {
        UseCamera(onBack = { openCamera.value = false }) { uri ->
            //La logica cuando tengo la foto
            //salgo de la camara
            openCamera.value = false
            newUriPicture.value = uri
        }
    }

    if (openGallery.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        ) {
            SelectImageFromGallery(onGalleryClosed = {
                openGallery.value = false
            }) { uri ->
                newUriPicture.value = uri
                openGallery.value = false
            }
        }
    }

    val changeUbication = remember {
        mutableStateOf(false)
    }

    if (changeUbication.value) {
        MapViewUI(
            coordinates.latitude,
            coordinates.longitude,
            searchMapVM,
            mapVM,
            navController,
            actionClose = {
                changeUbication.value = false //Close Map
            }) {
            //Cambiar ubicacion en la memoria del celular
            scope.launch {
                //ubicationDataStore.saveUbication(it) /*La ubicacion de la propuesta debe ser independiente*/
                vmNewPublication.setUbication(it.latitude, it.longitude)
                changeUbication.value = false //close map
            }
        }
    }

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) { scope ->
        Scaffold(modifier = modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                Column {
                    TopBar(title = "Nueva Propuesta", scope = scope, drawerState = drawerState)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangePrincipal)
                    ) {
                        ButtonUbication(
                            address,
                            textColor = Color.White
                        ){
                            changeUbication.value = true //Open Map
                        }
                    }
                }
            }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 0.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleNewPublication()
                Space(size = 5.dp)
                DataProposal(
                    vmProfession,
                    vmNewPublication,
                    token,
                    showError,
                    openAlertSelectImage,
                    newUriPicture
                ) {
                    ButtonPrincipal(text = "Publicar", enabled = buttonEnabled) {
                        token?.let { loginToken ->
                            vmLoading.withLoading {
                                vmNewPublication.createProposal(
                                    context,
                                    newUriPicture.value,
                                    token = loginToken.token,
                                    {
                                        //MOSTRAR ERROR
                                        showError.value = true
                                    }) { id ->
                                    //IR A LA NUEVA PROPUESTA
                                    navController.navigate("Proposal/${id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleNewPublication() {
    Text(
        text = "Cuenta de que trata tu propuesta",
        color = OrangePrincipal,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
fun DataProposal(
    vmProfession: ProfessionsViewModel,
    vmNewPublication: NewPublicationViewModel,
    token: LoginToken?,
    showError: MutableState<Boolean>,
    openAlertSelectImage: MutableState<Boolean>,
    newUriPicture: MutableState<Uri>,
    bottomPart: @Composable () -> Unit
) {
    val title: String by vmNewPublication.title
    val description: String by vmNewPublication.description
    val requirements: String by vmNewPublication.requirements
    val priceFrom: String by vmNewPublication.priceStart
    val priceTo: String by vmNewPublication.priceUntil

    Box {
        SearchField(vmProfession = vmProfession, vmNewPublication = vmNewPublication)

        Column(
            modifier = Modifier
                .offset(y = 100.dp)
        ) {
            FieldWithLabel("Título", title, "Título") {
                vmNewPublication.setData(it, description, requirements)
            }
            Space(size = 8.dp)

            FieldAreaWithLabel("Da una breve descripción", description, "...") {
                if (it.length <= 500) vmNewPublication.setData(title, it, requirements)
            }

            Space(size = 8.dp)

            FieldAreaWithLabel(
                "Requerimientos que crees imprescindibles",
                requirements,
                "Detalla sobre el trabajo, ej, si vas a querer una mesa, que tipo de madera usar, medidas, etc."
            ) {
                if (it.length <= 255) vmNewPublication.setData(title, description, it)
            }

            Space(size = 8.dp)

            SelectBudget(
                priceFrom,
                priceTo
            ) { priceF, priceT ->
                vmNewPublication.setBudget(priceF, priceT)
            }
            Space(size = 8.dp)
            //Photo
            SelectPhoto(newUriPicture) {
                openAlertSelectImage.value = true
            }

            Space(size = 8.dp)

            bottomPart()

            Space(size = 50.dp)
        }
    }
}

@Composable
fun SelectPhoto(newUriPicture: MutableState<Uri>, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(80.dp)
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
        ) {
            InsertAsyncImage(
                image = newUriPicture.value.toString(),
                R.drawable.camerapicnull,
                modifier = Modifier.fillMaxSize()
            ) {}
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Adjuntar imagen",
                color = GrayText,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
fun SelectBudget(priceFrom: String, priceTo: String, onValueChange: (String, String) -> Unit) {

    Column {
        Text(text = "Presupuesto")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Desde", color = GrayField)
                CommonField(
                    text = priceFrom,
                    placeholder = "ej. $10.000",
                    keyboardType = KeyboardType.Number
                ) {
                    val formattedValue = formatNumber(it)
                    onValueChange(formattedValue, priceTo)
                }
            }
            Space(size = 10.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Hasta", color = GrayField)
                CommonField(
                    text = priceTo,
                    placeholder = "ej. $20.000",
                    keyboardType = KeyboardType.Number
                ) {
                    val formattedValue = formatNumber(it)
                    onValueChange(priceFrom, formattedValue)
                }
            }
        }
    }
}


@Composable
fun FieldWithLabel(
    label: String,
    text: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label)
        CommonField(text = text, placeholder = placeholder) {
            onValueChange(it)
        }
    }
}

@Composable
fun FieldAreaWithLabel(
    label: String,
    text: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label)
        CommonFieldArea(text = text, placeholder = placeholder) {
            onValueChange(it)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchField(vmProfession: ProfessionsViewModel, vmNewPublication: NewPublicationViewModel) {
    val colors = listOf(
        Color(0xFF009688), // Teal 500
        Color(0xFF03A9F4), // Light Blue 500
        Color(0xFF8BC34A), // Light Green 500
        Color(0xFFFF5722), // Deep Orange 500
        Color(0xFFE91E63), // Pink 500
        Color(0xFF9C27B0), // Purple 500
    )
    val professions by vmProfession.professions
    val profession by vmNewPublication.profession
    var color by remember { mutableStateOf(GrayField) }
    var lastSelectedCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .zIndex(2f)
            .height(500.dp)
    ) {
        Text(text = "Profesional que buscas")

        //Elegir profesion
        SearchProfession(
            color = color,
            profession?.name ?: "",
            lastSelectedCount,
            onQueryChange = {
                //Buscar profesiones
                vmProfession.getProfessions(it)

                if (it.isEmpty()) {
                    vmNewPublication.setProfession(null)
                }
            }) {
            //Mostrar lista de profesiones
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 10.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                FlowRow(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = Int.MAX_VALUE,
                    maxLines = Int.MAX_VALUE,
                    overflow = FlowRowOverflow.Clip
                ) {
                    professions.forEach { profession ->
                        val colorRandom = colors.random()
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(colorRandom, shape = CircleShape)
                                .border(1.dp, Color.White, shape = CircleShape)
                                .clickable {
                                    //Poner el texto en el SearchProfession
                                    vmNewPublication.setProfession(profession)
                                    lastSelectedCount++
                                    color = colorRandom
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profession.name, color = Color.White, modifier = Modifier
                                    .padding(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}