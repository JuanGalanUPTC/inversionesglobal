package co.edu.uptc.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Contrato mínimo de persistencia para entidades del sistema.
 *
 * @param <T> tipo de entidad persistida
 */
public interface Repository<T> {
    /**
     * Persiste una entidad en el almacenamiento.
     *
     * @param entity entidad a guardar
     */
    void save(T entity);

    /**
     * Obtiene todas las entidades persistidas.
     *
     * @return lista (posiblemente vacía) de entidades
     */
    List<T> findAll();
    // Usamos Predicate para que la lógica le diga cómo buscar el ID
    /**
     * Busca una entidad que cumpla una condición.
     *
     * @param condition predicado de búsqueda
     * @return entidad encontrada o vacío si no existe
     */
    Optional<T> findBy(Predicate<T> condition); 

    /**
     * Elimina entidades que cumplan una condición.
     *
     * @param condition predicado de eliminación
     */
    void deleteBy(Predicate<T> condition);

    /**
     * Reemplaza el conjunto completo de datos persistidos.
     *
     * @param data nuevos datos a persistir
     */
    void replaceAll(List<T> data);
}