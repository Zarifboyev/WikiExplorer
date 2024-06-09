import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class WikimediaCommonsURLGeneratorTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val displayMetrics = mock(DisplayMetrics::class.java)
        `when`(context.resources).thenReturn(resources)
        `when`(resources.displayMetrics).thenReturn(displayMetrics)
    }

//    @Test
//    fun testGenerateFileURL_removesFilePrefix() {
//        val fileName = "Tour_Eiffel_Wikimedia_Commons.jpg"
//        val expectedThumbnailWidth = 100 // Mocking the expected thumbnail width
//        val expectedURL = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg/200px-Tour_Eiffel_Wikimedia_Commons.jpg"
//
//        // Mock the screen width calculation
//        val displayMetrics = context.resources.displayMetrics
//        val screenWidth = 300 // Mocking the screen width to be 300 pixels
//        displayMetrics.widthPixels = screenWidth
//
//        val generatedURL = WikimediaCommonsURLGenerator.generateFileURL(context, fileName)
//        assertEquals(expectedURL, generatedURL)
//    }
}
