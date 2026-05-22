package org.simpmc.simppay.menu.card;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.simpmc.simppay.config.ConfigManager;
import org.simpmc.simppay.config.types.CardConfig;
import org.simpmc.simppay.config.types.data.menu.DisplayItem;
import org.simpmc.simppay.config.types.data.menu.RoleType;
import org.simpmc.simppay.config.types.menu.card.CardListMenuConfig;
import org.simpmc.simppay.util.MessageUtil;

import java.util.Map;

public class CardListView extends View {


    private final State<Pagination> paginationState = buildLazyPaginationState(context -> {
        return ConfigManager.getInstance().getConfig(CardConfig.class).getEnabledCardTypes();

    }).elementFactory((ctx, bukkitItemComponentBuilder, i, telco) -> {
        ItemStack item = ConfigManager.getInstance()
                .getConfig(CardListMenuConfig.class)
                .cardItem.clone().replaceStringInName("{card_name}", telco.toString())
                .getItemStack(ctx.getPlayer());

        bukkitItemComponentBuilder.withItem(item).
                onClick(click -> {
                    // get current card session and add data, then move to next menu
                    click.openForPlayer(CardPriceView.class, telco);
                });

    }).build();


    @Override
    public void onInit(ViewConfigBuilder config) {
        config.cancelInteractions();
        config.layout(ConfigManager.getInstance().getConfig(CardListMenuConfig.class).layout.toArray(new String[0]));
    }

    @Override
    public void onOpen(@NotNull OpenContext open) {
        CardListMenuConfig menuConfig = ConfigManager.getInstance().getConfig(CardListMenuConfig.class);
        Pagination pagination = paginationState.get(open);
        open.modifyConfig().title(MessageUtil.getComponentParsed(menuConfig.title, open.getPlayer())); // Title support papi
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        CardListMenuConfig menuConfig = ConfigManager.getInstance().getConfig(CardListMenuConfig.class);

        Map<Character, DisplayItem> displayedItems = menuConfig.displayItems;

        for (Map.Entry<Character, DisplayItem> entry : displayedItems.entrySet()) {
            DisplayItem item = entry.getValue();
            if (item.getRole() == RoleType.NONE) {
                render.layoutSlot(entry.getKey(), entry.getValue().getItemStack(render.getPlayer()));
            }
            if (item.getRole() == RoleType.PREV_PAGE) {
                render.layoutSlot(entry.getKey(), item.getItemStack(render.getPlayer()))
                        .updateOnStateChange(paginationState)
                        .onClick((ctx) -> {
                            paginationState.get(ctx).back();
                        });
            }
            if (item.getRole() == RoleType.NEXT_PAGE) {
                render.layoutSlot(entry.getKey(), item.getItemStack(render.getPlayer()))
                        .updateOnStateChange(paginationState)
                        .onClick((ctx) -> {
                            paginationState.get(ctx).advance();
                        });
            }
        }
    }
}
