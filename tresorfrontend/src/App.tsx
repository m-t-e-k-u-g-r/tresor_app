import React from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import './App.css';
import './css/mvp.css';
import Home from './pages/Home';
import Layout from "./pages/Layout";
import NoPage from "./pages/NoPage";
import Users from './pages/user/Users';
import LoginUser from "./pages/user/LoginUser";
import RegisterUser from "./pages/user/RegisterUser";
import Secrets from "./pages/secret/Secrets";
import NewCredential from "./pages/secret/NewCredential";
import NewCreditCard from "./pages/secret/NewCreditCard";
import NewNote from "./pages/secret/NewNote";
import {LoginProvider} from "./context/login-context";
import {SecretProvider} from "./context/secret-context";

/**
 * App
 * @author Peter Rutschmann
 */
export default function App() {
    return (
        <BrowserRouter>
            <LoginProvider>
                <SecretProvider>
                    <Routes>
                        <Route path="/" element={<Layout />}>
                            <Route index element={<Home/>}/>
                            <Route path="/user/users" element={<Users />}/>
                            <Route path="/user/login" element={<LoginUser />}/>
                            <Route path="/user/register" element={<RegisterUser />}/>
                            <Route path="/secret/secrets" element={<Secrets />}/>
                            <Route path="/secret/newcredential" element={<NewCredential />}/>
                            <Route path="/secret/newcreditcard" element={<NewCreditCard />}/>
                            <Route path="/secret/newnote" element={<NewNote />}/>
                            <Route path="*" element={<NoPage/>}/>
                        </Route>
                    </Routes>
                </SecretProvider>
            </LoginProvider>
        </BrowserRouter>
    )
}
