import {Outlet, Link} from "react-router-dom";
import {useLogin} from "../context/login-context";
import {useEffect} from "react";

/**
 * Layout
 * @author Peter Rutschmann
 */
const Layout = () => {
    const { logout } = useLogin();
    const { checkLogin, loginValues, userId, isAdmin } = useLogin();
    const displayAdmin = userId != null && isAdmin;

    useEffect(() => {
        checkLogin();
    }, [])

    function userNav() {
        if (userId != null) {
            return (
                <ul>
                    <li><Link to="/user/changepassword">Change password</Link></li>
                    <li><a onClick={() => logout()}>logout</a></li>
                </ul>
            )
        } else {
            return (
                <ul>
                    <li><Link to="/user/register">register</Link>
                    </li><li><Link to="/user/login">login</Link></li>
                </ul>
            )
        }
    }

    return (
        <>
            <nav>
                <h1>The secret tresor application</h1>
                <p>{userId === null ? 'No user logged in' : `user ${userId}: ` + loginValues.email}</p>
                <ul>
                    {userId != null &&
                        <li><span>Secrets</span>
                            <ul>
                                <li><Link to="/secret/secrets">my secrets</Link></li>
                                <li><Link to="/secret/newcredential">new credential</Link></li>
                                <li><Link to="/secret/newcreditcard">new credit-card</Link></li>
                                <li><Link to="/secret/newnote">new note</Link></li>
                            </ul>
                        </li>
                    }
                    <li><span>User</span>
                        { userNav() }
                    </li>
                    {displayAdmin &&
                        <li><span>Admin</span>
                            <ul>
                                <li><Link to="/user/users">All users</Link></li>
                                <li>Add user</li>
                                <li><Link to="/user/users/:id">Edit user</Link></li>
                                <li>All secrets</li>
                            </ul>
                        </li>
                    }
                    <li>
                        <Link to="/">About</Link>
                    </li>
                </ul>
            </nav>
            <hr/>
            <Outlet/>
        </>
    )
};

export default Layout;