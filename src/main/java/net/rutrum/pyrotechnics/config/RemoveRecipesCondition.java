package net.rutrum.pyrotechnics.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;

import net.rutrum.pyrotechnics.Pyrotechnics;

public record RemoveRecipesCondition(boolean shouldRemove) implements ResourceCondition {

    public static final MapCodec<RemoveRecipesCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            MapCodec.unit(PyrotechnicsConfig.getInstance().removeVanillaRecipes)
                .forGetter(RemoveRecipesCondition::shouldRemove)
        ).apply(instance, RemoveRecipesCondition::new)
    );

    public static final ResourceConditionType<RemoveRecipesCondition> TYPE = ResourceConditionType.create(
        Identifier.fromNamespaceAndPath(Pyrotechnics.MOD_ID, "remove_recipes"),
        CODEC
    );

    public static void register() {
        ResourceConditions.register(TYPE);
    }

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(RegistryInfoLookup registryInfoLookup) {
        return shouldRemove;
    }
}