/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.domain.model

@ConsistentCopyVisibility
data class ChecksumType private constructor(
    val digest: String,
    val name: String = digest
) {
    companion object {
        val MD5 = ChecksumType("MD5")
        val SHA_1 = ChecksumType("SHA-1")
        val SHA_224 = ChecksumType("SHA-224")
        val SHA_256 = ChecksumType("SHA-256")
        val SHA_384 = ChecksumType("SHA-384")
        val SHA_512 = ChecksumType("SHA-512")

        private var securityMessageDigests: List<String>? = null

        fun registerSecurityMessageDigests(digests: List<String>) {
            if (!securityMessageDigests.isNullOrEmpty()) {
                throw IllegalArgumentException("SecurityMessageDigests already registered")
            }
            securityMessageDigests = digests.distinctBy { it.replace("OID.", "") }
        }

        val entries: List<ChecksumType> by lazy {
            val available = securityMessageDigests?.mapNotNull { messageDigest ->
                if (messageDigest.isEmpty()) null
                else {
                    val digest = messageDigest.replace("OID.", "")
                    KnownOIDs.findMatch(digest)?.let { oid ->
                        ChecksumType(
                            digest = messageDigest,
                            name = oid.stdName
                        )
                    } ?: ChecksumType(digest = messageDigest)
                }
            }?.sortedBy { it.digest } ?: emptyList()

            listOf(
                MD5,
                SHA_1,
                SHA_224,
                SHA_256,
                SHA_384,
                SHA_512,
            ).let {
                it + available
            }.distinctBy { it.name.replace("-", "") }
        }

        fun fromString(
            digest: String?
        ): ChecksumType? = digest?.let {
            entries.find {
                it.digest == digest
            }
        }
    }
}