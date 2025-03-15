package com.starshootercity.abilities;

import com.starshootercity.OriginSwapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VisibleAbility extends Ability {

    default List<OriginSwapper.LineData.LineComponent> getUsedDescription() {
        return getDescription();
    }

    default List<OriginSwapper.LineData.LineComponent> getUsedTitle() {
        return getTitle();
    }

    default void setupTranslatedText() {
        registerTranslation("title", title());
        registerTranslation("description", description());
    }

    @NotNull
    default List<OriginSwapper.LineData.LineComponent> getDescription() {
        return OriginSwapper.LineData.makeLineFor(modifyDescription(translate("description")), OriginSwapper.LineData.LineComponent.LineType.DESCRIPTION);
    }

    /**
     * Default description of the ability, can be configured in translations.yml
     * @return The default description
     */
    String description();

    @NotNull
    default List<OriginSwapper.LineData.LineComponent> getTitle() {
        return OriginSwapper.LineData.makeLineFor(modifyTitle(translate("title")), OriginSwapper.LineData.LineComponent.LineType.TITLE);
    }

    /**
     * Default title of the ability, can be configured in translations.yml
     * @return The default title
     */
    String title();

    /**
     * Modify the description after translation
     * @param description Translated description
     * @return Modified description
     */
    default String modifyDescription(String description) {
        return description;
    }

    /**
     * Modify the title after translation
     * @param title Translated title
     * @return Modified title
     */
    default String modifyTitle(String title) {
        return title;
    }
}
