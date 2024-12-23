package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.model.busco.Proposal
import kotlinx.coroutines.flow.MutableSharedFlow

class ProposalsDataSource(
    private val repo: ProposalsRepository,
    userId: Int = 0,
    status: Boolean?,
    tokenP: String? = null,
    function: String? = null,
    queryP: String? = null,
    categoryIdP: Int? = null,
    ubicationP: LatLng? = null
) : PagingSource<Int, Proposal>() {
    private val userId2 = userId
    private val status2 = status
    private val functionCall = function
    private val token = tokenP
    private val ubication = ubicationP
    private val query = queryP
    private val categoryId = categoryIdP

    val totalRecordsFlow = MutableSharedFlow<Int>()

    //controla el estado de paginacion
    override fun getRefreshKey(state: PagingState<Int, Proposal>): Int? {
        // Obtiene la posición del ancla en el estado de paginación
        return state.anchorPosition?.let { anchorPosition ->
            // Encuentra la página más cercana a la posición del ancla
            val anchorPage = state.closestPageToPosition(anchorPosition)
            // Calcula la clave de la página para refrescar los datos
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    // Trae más datos cuando se requiere una nueva página
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Proposal> {
        return try {
            // Obtiene el número de la siguiente página o comienza con la página 1 si es nulo
            val nextPageNumber = params.key ?: 1
            // Hace una solicitud al repositorio para obtener las propuestas del usuario
            val response =
                if (functionCall == "search") {
                    repo.searchProposals(
                        token = token!!,
                        query = query!!,
                        categoryId,
                        page = nextPageNumber,
                        pageSize = 6,
                        ubication?.latitude,
                        ubication?.longitude
                    )
                } else if (functionCall != "getRecommendedProposals") {
                    repo.getProposalsOfUser(
                        userId2,
                        page = nextPageNumber,
                        pageSize = 6,
                        status = status2
                    )
                } else {
                    repo.getRecommendedProposals(
                        token!!,
                        page = nextPageNumber,
                        pageSize = 6,
                        ubication?.latitude,
                        ubication?.longitude
                    )
                }

            totalRecordsFlow.emit(response.numberOfRecords)

            // Devuelve los resultados cargados en una página
            LoadResult.Page(
                data = response.records, // Datos de la respuesta
                prevKey = null, //Sin pagina anterior
                nextKey = if (response.records.isEmpty()) null else nextPageNumber + 1 // Determina la siguiente clave
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}