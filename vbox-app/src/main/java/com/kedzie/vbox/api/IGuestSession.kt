package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.DirectoryCreateFlag
import com.kedzie.vbox.api.jaxb.DirectoryOpenFlag
import com.kedzie.vbox.api.jaxb.DirectoryRemoveRecFlag
import com.kedzie.vbox.api.jaxb.FsObjRenameFlag
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

/**
 * A guest session represents one impersonated user account on the guest, so every operation will use the same credentials specified when
 * creating the session object via {@link IGuest#createSession}.
 */
@KsoapProxy
@Ksoap
interface IGuestSession: IManagedObjectRef, Parcelable {
    /**
     * Returns the user name used by this session to impersonate users on the guest.
     */
    @Cacheable("User")
	suspend fun getUser(): String

    /**
     * Returns the domain name used by this session to impersonate users on the guest.
     */
    @Cacheable("Domain")
	suspend fun getDomain(): String

    /**
     * Returns the session's friendly name.
     */
    @Cacheable("Name")
	suspend fun getName(): String

    /**
     * Returns the internal session ID.
     */
    @Cacheable("Id")
	suspend fun getId(): Int

    /**
     * Returns the session timeout (in ms).
     * <dl class="user"><dt><b>Expected result codes:</b></dt><dd><table class="doxtable">
     * <tbody><tr>
     * <td>{@link IVirtualBox#E_NOTIMPL} </td><td>The method is not implemented yet.  </td></tr>
     * </tbody></table>
     * </dd></dl>
     */
    @Cacheable("Timeout")
	suspend fun getTimeout(): Int

    suspend fun setTimeout(@Cacheable("Timeout") @Ksoap(type="unsignedint") timeout: Int)

    /**
     * Returns the current session environment.
     */
    @Cacheable("Environment")
	suspend fun getEnvironment(): List<String>

    suspend fun setEnvironment(@Cacheable("Environment") environment: List<String>)

    /**
     * Returns all current guest processes.
     */
    @Cacheable("Processes")
	suspend fun getProcesses(): List<IGuestProcess>

    /**
     * Returns all currently opened guest directories.
     */
    @Cacheable("Directories")
	suspend fun getDirectories(): List<IGuestDirectory>

    /**
     * Returns all currently opened guest files.
     */
    @Cacheable("Files")
	suspend fun getFiles(): List<IGuestFile>

    /**
     * Closes this session.
     * All opened guest directories, files and processes which are not referenced by clients anymore will be uninitialized.
     */
    suspend fun close()

    /**
     * Copies a file from guest to the host.
     * @param source    Source file on the guest to copy to the host.
     *
     * @param destination    Destination file name on the host.
     * @param flags        Copy flags; see [CopyFileFlag](_virtual_box_8idl.html#af4001a07f3e4bc28ecc98faf1d6c7635)**** for more information.
     * @return        Progress object to track the operation completion.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Error starting the copy operation.</td></tr>
    </tbody></table></dd></dl> *
     */
    suspend fun copyFromGuest(sources: List<String>, filters: List<String>, flags: List<String>, destination: String): IProgress

    /**
     * Copies a file from host to the guest.
     * @param sources    Source file on the host to copy to the guest.
     * @param filters    filters
     * @param flags    Copy flags;
     * @return    Progress object to track the operation completion.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Error starting the copy operation</td></tr>
    </tbody></table></dd></dl> *
     */
    suspend fun copyToGuest(sources: List<String>, filters: List<String>, flags: List<String>, destination: String): IProgress

    /**
     * Create a directory on the guest.
     * @param path        Full path of directory to create.
     * @param mode        File creation mode.
     * @param flags        Creation flags; see [DirectoryCreateFlag] for more information.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Error while creating the directory.</td></tr>
    </tbody></table></dd></dl> *
     */
    suspend fun directoryCreate( path: String, @Ksoap(type = "unsignedint") mode: Int,  vararg flags: DirectoryCreateFlag)

    /**
     * Create a temporary directory on the guest.
     * @param templateName        Template for the name of the directory to create. This must contain at least one 'X' character. The first group of consecutive 'X' characters in the template will be replaced by a random alphanumeric string to produce a unique name.
     * @param mode        The mode of the directory to create. Use 0700 unless there are reasons not to. This parameter is ignored if "secure" is specified.
     * @param path        The absolute path to create the temporary directory in.
     * @param secure        Whether to fail if the directory can not be securely created. Currently this means that another unprivileged user cannot manipulate the path specified or remove the temporary directory after it has been created. Also causes the mode specified to be ignored. May not be supported on all guest types.
     * @return        On success this will contain the name of the directory created with full path.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>The temporary directory could not be created. Possible reasons include a non-existing path or an insecure path when the secure option was requested</td></tr>
     * <td>[IVirtualBox.VBOX_E_NOT_SUPPORTED]</td><td>The operation is not possible as requested on this particular guest type.</td>
     * <td>[IVirtualBox.E_INVALIDARG]</td><td>Invalid argument. This includes an incorrectly formatted template, or a non-absolute path. </td>
    </tbody></table></dd></dl> *
     */
    suspend fun directoryCreateTemp(templateName: String, @Ksoap(type = "unsignedint") mode: Int,
                                     path: String, @Ksoap(type = "boolean") secure: Boolean): String

    /**
     * Checks whether a directory exists on the guest or not.
     * @param path        Directory to check existence for.
     * @return                Returns `true` if the directory exists, `false` if not.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Error while checking existence of the directory specified. </td></tr>
    </tbody></table></dd></dl> *
     */
    suspend fun directoryExists(path: String): Boolean

    /**
     * Opens a directory and creates a [IGuestDirectory] object that can be used for further operations.
     * @param path        Full path to file to open.
     * @param filter        Open filter to apply. This can include wildcards like ? and *.
     * @param flags        Open flags; see [DirectoryOpenFlag] for more information.
     * @return            [IGuestDirectory] object containing the opened directory.
     */
    suspend fun directoryOpen(path: String, filter: String, vararg flags: DirectoryOpenFlag): IGuestDirectory

    /**
     * Queries information of a directory on the guest.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.VBOX_E_OBJECT_NOT_FOUND]</td><td>Directory to query information for was not found.</td></tr>
     * <td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Error querying information.</td>
    </tbody></table></dd></dl> *
     * @param path        Directory to query information for.
     * @return        information
     */
    suspend fun directoryQueryInfo(path: String): IGuestFsObjInfo

    suspend fun directoryRemove(path: String)

    suspend fun directoryRemoveRecursive(path: String, vararg flags: DirectoryRemoveRecFlag): IProgress


    suspend fun directorySetACL(path: String, acl: String)

    suspend fun environmentClear()

    suspend fun environmentGet(name: String): String

    suspend fun environmentSet(name: String, value: String)

    suspend fun environmentUnSet(name: String)

    suspend fun fileCreateTemp(templateName: String, @Ksoap(type = "unsignedint") mode: Int, path: String, @Ksoap(type = "boolean") secure: Boolean): IGuestFile

    suspend fun fileExists(path: String): Boolean

    suspend fun fileRemove(path: String)

    suspend fun fileOpen(path: String, openMode: String, disposition: String,
                          @Ksoap(type = "unsignedint") creationMode: Int, @Ksoap(type = "long") offset: Long): IGuestFile

    suspend fun fileQueryInfo(path: String): IFsObjInfo

    suspend fun fsObjRename(oldPath: String, newPath: String, vararg flags: FsObjRenameFlag)

    suspend fun fileQuerySize(path: String): Long

    suspend fun fileSetACL(path: String, acl: String)

    /**
     * Removes a symbolic link on the guest if it's a file.
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.E_NOTIMPL]</td><td>The method is not implemented yet.  </td></tr>
    </tbody></table></dd></dl> *
     * @param file        Symbolic link to remove.
     */
    suspend fun symlinkRemoveFile(file: String)
}