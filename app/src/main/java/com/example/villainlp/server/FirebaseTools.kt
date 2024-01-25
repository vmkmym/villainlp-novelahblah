package com.example.villainlp.server

import android.content.ContentValues
import android.util.Log
import com.example.villainlp.novel.Book
import com.example.villainlp.novel.NovelInfo
import com.example.villainlp.novel.RelayChatToNovelBook
import com.example.villainlp.novel.library.comment.Comment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseTools {

    fun saveBook(book: Book) {
        val db = FirebaseFirestore.getInstance()

        val newDocRef = db.collection("Library").document()
        val documentId = newDocRef.id
        val updateBook = book.copy(documentID = documentId)
        newDocRef.set(updateBook)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }


    fun updateBookRating(documentId: String, totalRate: Float, starCount: Int) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val documentReference = db.collection("Library").document(documentId)
        // 문서가져오기
        documentReference.get().addOnSuccessListener { _ ->
            // 업데이트할 데이터 생성
            val rate = totalRate / starCount

            val updates = hashMapOf<String, Any>(
                "totalRate" to totalRate,
                "starCount" to starCount,
                "rating" to rate
            )
            // 문서 업데이트
            documentReference.update(updates)
                .addOnSuccessListener {
                    // 업데이트 성공
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot updated with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    // 업데이트 실패
                    Log.w(ContentValues.TAG, "Error updating document", e)
                }
        }
    }

    suspend fun getLibraryBookFromFirestore(documentId: String): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library").document(documentId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val book = documentSnapshot.toObject(Book::class.java)
                    result.add(book!!)
                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully retrieved: $book")
                    continuation.resume(result)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error getting document", e)
                    continuation.resumeWithException(e)
                }
        }

    suspend fun getRatingFromFirestore(documentId: String): Float? =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()

            db.collection("Library").document(documentId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val rating = documentSnapshot.getDouble("rating")?.toFloat()
                    continuation.resume(rating)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error getting document", e)
                    continuation.resumeWithException(e)
                }
        }

    suspend fun getCommentCount(documentId: String): Int {
        val db = FirebaseFirestore.getInstance()
        val commentCollection = db.collection("Library").document(documentId).collection("comment")

        val snapshot = commentCollection.get().await()
        return snapshot.size()
    }

    fun updateBookViews(documentId: String, views: Int) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val documentReference = db.collection("Library").document(documentId)
        // 업데이트할 데이터 생성
        val updates = hashMapOf<String, Any>(
            "views" to views
        )
        // 문서 업데이트
        documentReference.update(updates)
            .addOnSuccessListener {
                // 업데이트 성공
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot updated with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                // 업데이트 실패
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    fun updateCommentCount(documentId: String, commentCount: Int) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val documentReference = db.collection("Library").document(documentId)
        // 업데이트할 데이터 생성
        val updates = hashMapOf<String, Any>(
            "commentCount" to commentCount
        )
        // 문서 업데이트
        documentReference.update(updates)
            .addOnSuccessListener {
                // 업데이트 성공
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot updated with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                // 업데이트 실패
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    fun uploadComment(documentId: String, comment: Comment) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val newDocRef =
            db.collection("Library").document(documentId).collection("comment").document()
        val commentDocumentId = newDocRef.id
        val updateComment = comment.copy(documentID = commentDocumentId)
        // 코멘트 업로드
        newDocRef.set(updateComment)
            .addOnSuccessListener {
                // 업로드 성공
                Log.d(
                    ContentValues.TAG,
                    "Comment uploaded with ID: $commentDocumentId"
                )
            }
            .addOnFailureListener { e ->
                // 업로드 실패
                Log.w(ContentValues.TAG, "Error uploading comment", e)
            }
    }

    suspend fun fetchCommentsFromFirestore(documentId: String): List<Comment> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Comment>()

            db.collection("Library").document(documentId).collection("comment")
                .orderBy("uploadDate", Query.Direction.ASCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val commentData = document.toObject(Comment::class.java)
                        result.add(commentData)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    fun saveNovelInfo(novelInfo: NovelInfo) {
        val db = FirebaseFirestore.getInstance()

        // Add a new document
        val newDocRef = db.collection("NovelInfo").document()

        // Get the ID of the new document
        val documentId = newDocRef.id

        // Update the novelInfo object with the document ID
        val updatedNovelInfo = novelInfo.copy(documentID = documentId)

        // Set the document data
        newDocRef.set(updatedNovelInfo)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun saveChatToNovel(relayChatToNovel: RelayChatToNovelBook) {
        val db = FirebaseFirestore.getInstance()

        val newDocRef = db.collection("MyBookData").document()
        val documentId = newDocRef.id
        val updateRelayChatToNovel = relayChatToNovel.copy(documentID = documentId)
        newDocRef.set(updateRelayChatToNovel)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }


    fun deleteBookFromFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("MyBookData").document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun deleteChattingFromFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("NovelInfo").document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun deleteLibraryBookFromFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Library").document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun deleteCommentFromFirestore(documentId: String, commentDocumentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Library").document(documentId).collection("comment")
            .document(commentDocumentId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }


    suspend fun fetchNovelInfoDataFromFirestore(userId: String): List<NovelInfo> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<NovelInfo>()

            db.collection("NovelInfo")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val novelInfoId = document.id
                        val novelInfo = document.toObject(NovelInfo::class.java)
                        if (novelInfo.userID == userId) {
                            val bookWithId = NovelInfo(
                                novelInfo.title,
                                novelInfo.assistId,
                                novelInfo.threadId,
                                novelInfo.userID,
                                novelInfo.createdDate,
                                novelInfoId
                            )
                            result.add(bookWithId)
                        }
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun myNovelDataFromFirestore(userId: String): List<RelayChatToNovelBook> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<RelayChatToNovelBook>()

            db.collection("MyBookData")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(RelayChatToNovelBook::class.java)
                        if (bookData.userID == userId) {
                            val bookWithId = RelayChatToNovelBook(
                                bookData.title,
                                bookData.author,
                                bookData.script,
                                bookData.userID,
                                bookData.rating,
                                bookData.createdDate,
                                bookId
                            )
                            result.add(bookWithId)
                        }
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun novelDataSortingByRatingFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookWithId = convertDocumentToBook(document)
                        result.add(bookWithId)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun novelDataSortingByUploadDateFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library")
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookWithId = convertDocumentToBook(document)
                        result.add(bookWithId)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }


    suspend fun novelDataSortingByViewsFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library")
                .orderBy("views", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookWithId = convertDocumentToBook(document)
                        result.add(bookWithId)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    private fun convertDocumentToBook(document: DocumentSnapshot): Book {
        val bookData = document.toObject(Book::class.java)!!
        return Book(
            bookData.title,
            bookData.author,
            bookData.description,
            bookData.script,
            bookData.userID,
            bookData.rating,
            bookData.views,
            bookData.starCount,
            bookData.totalRate,
            bookData.uploadDate,
            bookData.commentCount,
            document.id
        )
    }
}