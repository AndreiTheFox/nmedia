package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
    @Query(
        """
                UPDATE PostEntity SET
                likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id;
                """
    )
    suspend fun likeById(id: Long)
}
//    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
//    fun updateContentByUd(id: Long, content: String)

//    fun save(post: PostEntity) =
//        if (post.id == 0L) insert(post) else updateContentByUd(post.id, post.content)


//    @Query("DELETE FROM PostEntity WHERE id = :id")
//    fun removeById(id: Long)
//    @Query ("""
//                UPDATE PostEntity SET
//                shares = shares +1,
//                sharedByMe = 1
//                WHERE id = :id;
//            """
//            )
//    fun sharePost(id: Long)