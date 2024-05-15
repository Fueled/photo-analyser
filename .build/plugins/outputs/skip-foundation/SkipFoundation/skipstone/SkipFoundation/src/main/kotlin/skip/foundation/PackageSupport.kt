package skip.foundation

import skip.lib.*


internal val <E0, E1> Tuple2<E0, E1>.inserted: E0
    get() = element0

internal val <E0, E1> Tuple2<E0, E1>.key: E0
    get() = element0

internal val <E0, E1> Tuple2<E0, E1>.memberAfterInsert: E1
    get() = element1

internal val <E0, E1> Tuple2<E0, E1>.value: E1
    get() = element1
