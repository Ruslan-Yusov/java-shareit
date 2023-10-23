package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    @Query(value =
            "select b.* from shareit.booking b join shareit.item i on b.item_id = i.id and owner_user_id = :id" +
                    " where :state = 'ALL'" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b join shareit.item i on b.item_id = i.id and owner_user_id = :id" +
                    " where :state = 'FUTURE' and b.start_dt > now()" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b join shareit.item i on b.item_id = i.id and owner_user_id = :id" +
                    " where :state = 'PAST' and b.end_dt < now()" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b join shareit.item i on b.item_id = i.id and owner_user_id = :id" +
                    " where :state = 'CURRENT' and b.end_dt >= now() and b.start_dt <= now()" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b join shareit.item i on b.item_id = i.id and owner_user_id = :id" +
                    " where :state in ('WAITING', 'REJECTED') and b.status = :state",
            nativeQuery = true)
    List<BookingEntity> findByOwnerIdAndState(@Param("id") Integer id, @Param("state") String state);

    @Query(value =
            "select b.* from shareit.booking b" +
                    " where :state = 'ALL' and b.booker_user_id = :id" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b" +
                    " where :state = 'FUTURE' and b.start_dt > now() and b.booker_user_id = :id" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b" +
                    " where :state = 'PAST' and b.end_dt < now() and b.booker_user_id = :id" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b" +
                    " where :state = 'CURRENT' and b.end_dt >= now() and b.start_dt <= now() and b.booker_user_id = :id" +
                    " UNION ALL" +
                    " select b.* from shareit.booking b" +
                    " where :state in ('WAITING', 'REJECTED') and b.status = :state and b.booker_user_id = :id",
            nativeQuery = true)
    List<BookingEntity> findByBookerIdAndState(@Param("id") Integer id, @Param("state") String state);

    @Query(nativeQuery = true, value = "select a.id," +
            " a.start_dt," +
            " a.end_dt," +
            " a.item_id," +
            " a.booker_user_id," +
            " a.status " +
            "from " +
            "(select t.*,  " +
            " (t.start_dt < now()) as is_past, " +
            " dense_rank() over (partition by(t.start_dt < now()) order by t.start_dt) as cnt_next, " +
            " dense_rank() over (partition by(t.start_dt < now()) order by t.start_dt desc) as cnt_prev  " +
            "from shareit.booking as t " +
            "where  " +
            "  t.item_id = :id and " +
            "  t.status = 'APPROVED'" +
            ") a " +
            "where " +
            " (a.is_past and a.cnt_prev = 1) or (not a.is_past and a.cnt_next = 1) ")
    List<BookingEntity> findBookingsByItemId(@Param("id") Integer itemId);

    @Query(" select count(*) " +
            " from BookingEntity " +
            " where " +
            " booker.id = :user_id and " +
            " item.id = :item_id and " +
            " status = 'APPROVED' and " +
            " endDateTime < now() ")
    Integer countBookingByItemIdAndBookerId(@Param("item_id") Integer id,
                                            @Param("user_id") Integer userId);
}
