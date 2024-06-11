import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class WikimediaCommonsURLGeneratorTest {

    private lateinit var context: Context

    @BeforeEach
    fun setUp() {
        context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val displayMetrics = mock(DisplayMetrics::class.java)
        `when`(context.resources).thenReturn(resources)
        `when`(resources.displayMetrics).thenReturn(displayMetrics)
    }
}
