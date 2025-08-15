package com.crazy.back.util.pagination;

import com.crazy.back.clients.swapi.response.SwGenericPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositePaginationHelper {

    public static <T> Page<T> fetchAndCompose(
            int externalPageSize,
            Pageable pageable,
            Function<Integer, SwGenericPage<T>> fetchFunction
    ) {
        int pageSize = pageable.getPageSize();// 15 por página
        int requestedPage = pageable.getPageNumber(); // la página que nos ha solicitado front, 0 based

        int startIndex = requestedPage * pageSize; // desde qué índice global queremos empezar
        //Por ejemplo, si pageSize es 15 y requestedPage es 2, startIndex será 30
        //Pero si es 0, startIndex será 0
        //Al final, son los elementos que queremos traer desde la página externa. A partir del elemento que queremos traer

        //Divimos el elemento por el que queremos empezar partido el tamaño de cada página de la API externa.
        //Le sumamos 1 porque las páginas de SWAPI empiezan en 1, no en 0.
        int startExternalPage = (startIndex / externalPageSize) + 1;

        //Operador módulo porque queremos saber cuántos elementos tenemos que saltar en la primera página externa
        //Por ejemplo, si starIndex es 15 y externalPageSize es 10, startIndex % externalPageSize será 5.
        //Porque 15/10 da 1 y el resto es 5. Queremos ese 5 que es el offset.
        int offset = startIndex % externalPageSize;

        List<T> aggregated = new ArrayList<>();
        int totalCount = 0;

        int swapiPage = startExternalPage;
        boolean firstLoop = true;

        //Mientras no hemos completado la página solicitada seguimos iterando
        while (aggregated.size() < pageSize) {
            // Llamamos a la función que nos trae la página de SWAPI
            SwGenericPage<T> resp = fetchFunction.apply(swapiPage);
            List<T> results = resp.getResults() != null ? resp.getResults() : List.of();

            // En la primera página externa puede que tengamos que “saltar” offset elementos
            int from = firstLoop ? Math.min(offset, results.size()) : 0;

            for (int i = from; i < results.size() && aggregated.size() < pageSize; i++) {
                aggregated.add(results.get(i));
            }

            totalCount = resp.getCount();      // SWAPI nos da el total global
            firstLoop = false;

            // ¿Ya completamos 15? ¿No hay next? cortamos
            if (aggregated.size() >= pageSize || resp.getNext() == null) break;

            //Al terminar la página, incrementamos el número de página de SWAPI
            swapiPage++;
        }

        return new PageImpl<>(aggregated, pageable, totalCount);
    }
}
