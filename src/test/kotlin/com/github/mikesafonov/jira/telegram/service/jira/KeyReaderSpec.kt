package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.service.jira.oauth.KeyReader
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.properties.Gen
import io.kotest.properties.string

/**
 * @author Mike Safonov
 */
class KeyReaderSpec : BehaviorSpec({
    val keyReader = KeyReader()
    Given("Key reader") {
        When("Valid private key") {
            Then("Read successfully") {
                val key = """MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPKLnKLpK6GO86Fiy3qm+/+98oj5Fzv5icJ70omuIvtgA2ZOk6+SPluuVB5t/5KPHGAiKKWpVC6O0VfhfaHLMxN9TXDLcSY+7Bs+tDWF2zlbr9/6DByCkjvuR8rZKp+7DH9oCVTe5nzD9zzBIEo1qEA8P5SLurYX0l1LoiDWCo1PAgMBAAECgYEA8Da7ZFGAZ9qXii+5jPkSvX/XVDc2/qwu9tIBe2Bevw2hcFLES++QGOb34sdYhkN8e+14ylGA0TebN+lYrsP8CRTC7+zO3c+rxSEwI7FIHOP3ANzBf8ukGDEejZfUmVi102J6VOw4l/YUVCk94kJYFYYaPtA9o5/MESawOKFn9xECQQD8Uu7wwQ4HWaTRLR2469Bx/o8Q6cXHt3u59etkXmbMx8xL/i0ahiG0VABGUjWLF8LMWSMTjJMTZOHYHxC46rLJAkEA9hQ1UOK3+5V9D960Lr/wGcaQShk4xx8AEAHoRA9Hg8rpyBIdd4P9busNsklbDkkqUn9uc0aiV9W5oYjE0HvzVwJAIeljg6fuor5BkHtnyzqmQSvdPoCsvDLExjFu+YZWep8/rdbzaOUuEnZXsxvJZnBDFgPE2xPBkJG2aL2EGdYxQQJAG1LhHKsf/Lp7mYCEB1Deqy4GJYQpbsq9agmcLbp4mWS1kraYOQYZ5PAKpEpK5wouvdxSelBUhGZB53LVT8UDnwJAcYB/uFSMWC9XQqY1haTMWjdfG4qdeSTuozPaLQzdS8D4uReuCjczyt0WsIeoln+lPBJPIpLKyqngiCbjbvGpfA=="""
                keyReader.readPrivateRsa(key) shouldNotBe null
            }
        }

        When("Invalid private key") {
            Then("Throw exception") {
                shouldThrowAny { keyReader.readPrivateRsa(Gen.string().random().first()) }
            }
        }
    }
})
