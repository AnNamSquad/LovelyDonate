package org.simpmc.simppay.handler.data;

import org.simpmc.simppay.handler.CardHandler;
import org.simpmc.simppay.handler.card.*;

public enum CardAPI {
    THESIEUTOC(TSTHandler.class),
    CARD2K(Card2KHandler.class),
    THESIEURECOM(TSRHandler.class),
    GT1SCOM(GT1SHandler.class),
    DOITHE1SVN(DT1SHandler.class);

    public final Class<? extends CardHandler> handlerClass;

    CardAPI(Class<? extends CardHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

}
