package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practica.buscov2.data.repository.busco.JobsRepository
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.model.busco.Proposal

class JobsDataSource(
    private val repo: JobsRepository,
    tokenP: String? = null,
    finishedP: Boolean? = null
) : PagingSource<Int, Proposal>() {
    private val token = tokenP
    private val finished = finishedP

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
    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Proposal> {
        return try {
            // Obtiene el número de la siguiente página o comienza con la página 1 si es nulo
            val nextPageNumber = params.key ?: 1
            // Hace una solicitud al repositorio para obtener las propuestas del usuario
            val response = repo.getJobs(
                token!!,
                page = nextPageNumber,
                pageSize = 6,
                finished
            )
            // Devuelve los resultados cargados en una página
            PagingSource.LoadResult.Page(
                data = response, // Datos de la respuesta
                prevKey = null, //Sin pagina anterior
                nextKey = if (response.isEmpty()) null else nextPageNumber + 1 // Determina la siguiente clave
            )
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }
}