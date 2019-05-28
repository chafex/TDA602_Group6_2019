/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

/**
 * A mapping class to lookup all Checkable instances currently
 * available in the application.
 */
enum class CheckMapping(val description: String) {
    PUBLIC_FILES_CHECK("Public files"),
    PERMISSIONS_CHECK("Permissions"),
    DELEGATION_CHECK("Permission delegation"),
    DUMMY_CHECK("Dummy"),
    EMPTY_DUMMY_CHECK("Empty dummy"),
    MICROPHONE_CHECK("Microphone check"),
    NETWORK_CHECK("Network check");

    companion object {
        /**
         * Maps a enum tag to an instance of that Checkable.
         */
        fun map(tag: CheckMapping): Checkable = when(tag) {
            PUBLIC_FILES_CHECK -> PublicFilesCheck
            CheckMapping.PERMISSIONS_CHECK -> PermissionsCheck
            CheckMapping.DELEGATION_CHECK -> DelegationCheck
            CheckMapping.DUMMY_CHECK -> DummyCheck
            CheckMapping.EMPTY_DUMMY_CHECK -> EmptyDummyCheck
            CheckMapping.MICROPHONE_CHECK -> MicrophoneCheck
            CheckMapping.NETWORK_CHECK -> NetworkCheck
        }
    }
}
