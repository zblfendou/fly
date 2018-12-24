package zbl.fly.base.vos;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
public class QueryResult<Item extends Serializable> implements Serializable {
    private long totalItemCount;
    private int totalPageCount;
    private List<Item> items;
    private int currentPage;

    public static <T extends Serializable> QueryResult<T> build(long totalElements,
                                                                int totalPages,
                                                                int number,
                                                                List<T> items) {
        QueryResult<T> queryResult = new QueryResult<>();
        queryResult.totalItemCount = totalElements;
        queryResult.totalPageCount = totalPages;
        queryResult.currentPage = number + 1;
        queryResult.items = items;
        return queryResult;
    }

    public static <T extends Serializable> QueryResult<T> empty() {
        return QueryResult.build(0, 0, 0, emptyList());
    }

    public <Output extends Serializable> QueryResult<Output> transform(Function<Item, Output> transformer) {
        return QueryResult.build(totalItemCount, totalPageCount, currentPage - 1, isEmpty(this.items) ?
                emptyList() :
                items.stream()
                        .map(transformer)
                        .collect(toList())
        );
    }

    public void forEach(Consumer<Item> consumer) {
        if (!isEmpty(this.items)) this.items.forEach(consumer);
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
