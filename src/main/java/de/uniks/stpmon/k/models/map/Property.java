package de.uniks.stpmon.k.models.map;

/**
 * @param name  Name of the property
 * @param type  Type of the property (string (default), int, float, bool, color, file, object or class
 *              (since 0.16, with color and file added in 0.17, object added in 1.4 and class added in 1.8))
 * @param value Value of the property
 */
public record Property(
        String name,
        String type,
        String value) {

}
