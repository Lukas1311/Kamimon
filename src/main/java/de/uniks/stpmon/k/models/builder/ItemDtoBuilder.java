package de.uniks.stpmon.k.models.builder;


import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.ItemUse;

public class ItemDtoBuilder {

    public static ItemDtoBuilder builder() {
        return new ItemDtoBuilder();
    }

    public static ItemDtoBuilder builder(ItemTypeDto type) {
        return builder().applyType(type);
    }

    private int id = 0;
    private String image = "";
    private String name = "";
    private int price = 0;
    private String description = "";
    private ItemUse itemUse = null;

    private ItemDtoBuilder() {
    }

    public ItemDtoBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public ItemDtoBuilder setImage(String imageUrl) {
        image = imageUrl;
        return this;
    }

    public ItemDtoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemDtoBuilder setPrice(int price) {
        this.price = price;
        return this;
    }

    public ItemDtoBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemDtoBuilder setItemUse(ItemUse itemUse) {
        this.itemUse = itemUse;
        return this;
    }

    public ItemDtoBuilder applyType(ItemTypeDto type) {
        return this.setId(type.id())
                .setImage(type.image())
                .setName(type.name())
                .setPrice(type.price())
                .setDescription(type.description())
                .setItemUse(type.use());
    }

    public ItemTypeDto create() {
        return new ItemTypeDto(id, image, name, price, description, itemUse);
    }

}
